package dong.lan.code.activity;
import android.app.*;
import android.os.*;
import dong.lan.code.*;
import android.support.v7.widget.*;
import dong.lan.code.adapter.*;
import dong.lan.code.bean.*;
import java.util.*;

public class CourseActivity extends Activity
{

	private RecyclerView cView;
	private CourseAdapter adapter;
	private List<Course> l = new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course);
		cView =(RecyclerView) findViewById(R.id.courseView);
		
		for(int i = 0;i<35;i++)
		{
			l.add(new Course(i+"",Math.random()*1000+""));
		}
		adapter = new CourseAdapter(this,l);
		GridLayoutManager gm = new GridLayoutManager(this,7,GridLayoutManager.VERTICAL,false);
	    cView.setLayoutManager(gm);
		cView.setAdapter(adapter);
		}
	
}
