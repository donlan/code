package dong.lan.code.bean;

public class Course
{
	private String tittle;
	private String des;

	
	public Course(String t,String d)
	{
		tittle =t;
		des=d;
	}
	public void setDes(String des)
	{
		this.des = des;
	}

	public String getDes()
	{
		return des;
	}


	public void setTittle(String tittle)
	{
		this.tittle = tittle;
	}

	public String getTittle()
	{
		return tittle;
	}}
