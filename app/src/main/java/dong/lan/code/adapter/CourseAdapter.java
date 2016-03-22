package dong.lan.code.adapter;
import android.content.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import dong.lan.code.*;
import dong.lan.code.bean.*;
import java.util.*;
import android.widget.TableLayout.*;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseHolder>
{

	private LayoutInflater inflater;
	private List<Course> lists = new ArrayList<>();
	private Context context;
	
	public CourseAdapter(Context c,List<Course> l)
	{
		this.context =c;
		this.lists =l;
		inflater = LayoutInflater.from(c);
	}
	@Override
	public CourseAdapter.CourseHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		
		return new CourseHolder(inflater.inflate(R.layout.item_course,null));
	}

	@Override
	public void onBindViewHolder(CourseAdapter.CourseHolder h, int pos)
	{
		h.topText.setText(lists.get(pos).getTittle());
		h.content.setText(lists.get(pos).getDes());
		int H = h.parent.getLayoutParams().height;
		/*
		if(H<50)
		{
			
			LayoutParams lp = (TableLayout.LayoutParams) h.parent.getLayoutParams();
			lp.height=(int) Math.random()*100+50;
			
			h.parent.setLayoutParams(lp);
		}
		*/
	}

	@Override
	public int getItemCount()
	{
		return lists.size();
	}
	
	
	public class CourseHolder extends RecyclerView.ViewHolder
	{
		TextView topText;
		EditText content;
		LinearLayout parent;
		public CourseHolder(View v)
		{
			super(v);
			topText=(TextView) v.findViewById(R.id.course_top);
			content=(EditText) v.findViewById(R.id.course_content);
			parent=(LinearLayout) v.findViewById(R.id.item_courseLayout);
		}
		
	}
}
