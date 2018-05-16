package entity;

public class User {
	private Integer id; 
	private String name;
	private String pwd;
	private Double money;
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", pwd=" + pwd + ", money=" + money + "]";
	}
	public User() {
		super();
	}
	public User(Integer id, String name, String pwd, Double money) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.money = money;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	
}
