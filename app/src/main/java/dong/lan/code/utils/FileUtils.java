package dong.lan.code.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import dong.lan.code.Interface.onCodeLoadListener;
import dong.lan.code.bean.Code;
import dong.lan.code.db.DBManager;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 4/29/2016  05:29.
 */
public class FileUtils {
    public static final int FROM = 1;
    public static final int TO = 2;
    public static final int FROM_DONE = 3;
    public static final int TO_DONE = 4;
    public static final int FROM_START = 7;
    public static final int FROM_BAD = 8;
    public static final int FROM_GOOD = 5;
     /*
    导出数据到SD卡
     */

    public static void dataToSD(Context context, File codeFile, TextView dataTo) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            StringBuilder stringBuffer = new StringBuilder();
            dataTo.setEnabled(false);
            List<Code> codes = DBManager.getInstance().getAllCodes();
            if (codes == null) {
                ToastUtil.Show(context, "没有保存的密码");
                return;
            }
            for (Code code : codes) {
                stringBuffer.append(code.getDes());
                stringBuffer.append("\n");
                stringBuffer.append(AES.encode(code.getWord()));
                stringBuffer.append("\n");
                stringBuffer.append(AES.encode(code.getOther()));
                stringBuffer.append("\n");
                stringBuffer.append(code.getCount());
                stringBuffer.append("\n");
            }
            try {
                if (!codeFile.exists()) {
                    if (!codeFile.createNewFile()) {
                        ToastUtil.Show(context, "创建导出文件失败");
                        dataTo.setEnabled(true);
                    }
                }
                ToastUtil.Show(context, "开始导出数据");
                FileWriter fileWriter = new FileWriter(codeFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(stringBuffer.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
                ToastUtil.Show(context, "导出数据成功");
                dataTo.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtil.Show(context, "导出数据失败");
                dataTo.setEnabled(true);
            }
        } else {
            ToastUtil.Show(context, "SD卡不存在");
            dataTo.setEnabled(true);
        }
    }


    /*
   从SD卡导入数据
    */
    public static void dataFromSD(Context context, TextView dataFrom, final List<Code> codes1, onCodeLoadListener codeLoadListener, final File codeFile, final Handler handler) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataFrom.setEnabled(false);
            codeLoadListener.onCodeChange(FROM_DONE, null);
            if (codeFile.exists()) {
                final SQLiteDatabase db = DBManager.getInstance().getHelper().getWritableDatabase();
                if (!db.isOpen()) {
                    ToastUtil.Show(context, "连接数据库出错");
                    handler.sendEmptyMessage(FROM_DONE);
                    return;
                }
                db.beginTransaction();
                try {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DBManager.getInstance().clearCode(db);
                                handler.sendEmptyMessage(FROM_START);
                                FileInputStream inputStream = new FileInputStream(codeFile);
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                String line;
                                int i = 0;
                                int index = 1;
                                Code code = null;
                                while ((line = bufferedReader.readLine()) != null) {
                                    i++;
                                    if (i % 4 == 1) {
                                        code = new Code();
                                        code.setDes(line);
                                    }
                                    if (i % 4 == 2) {
                                        assert code != null;
                                        code.setWord(line);

                                    }
                                    if (i % 4 == 3) {
                                        assert code != null;
                                        code.setOther(line);
                                    }
                                    if (i % 4 == 0) {
                                        assert code != null;
                                        code.setCount(index++);
                                        codes1.add(code);
                                    }
                                }
                                for (Code code1 : codes1) {
                                    DBManager.getInstance().saveDecodeCode(db,
                                            new Code(code1.getDes(),
                                                    code1.getWord(),
                                                    code1.getOther(),
                                                    code1.getCount()));
                                    code1.setWord(AES.decode(code1.getWord()));
                                    code1.setOther(AES.decode(code1.getOther()));
                                }
                                handler.sendEmptyMessage(FROM);
                                //db.setTransactionSuccessful();
                            } catch (IOException e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(FROM_BAD);

                            }
                        }
                    });
                    thread.start();
                } catch (Exception e) {
                    ToastUtil.Show(context, "读写数据的时候发生致命错误");
                    handler.sendEmptyMessage(FROM_DONE);
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
            } else {
                ToastUtil.Show(context, "没有已导出的文件");
                handler.sendEmptyMessage(FROM_DONE);
            }
        } else {
            ToastUtil.Show(context, "SD卡不存在");
            handler.sendEmptyMessage(FROM_DONE);
            dataFrom.setEnabled(true);
        }
    }

}
