package dong.lan.code.Interface;

import java.util.List;

import dong.lan.code.bean.Code;

/**
 * Created by Dooze on 2015/10/31.
 */
public interface onCodeLoadListener {
    void onCodeChange(int Tag,List<Code> codes);
}
