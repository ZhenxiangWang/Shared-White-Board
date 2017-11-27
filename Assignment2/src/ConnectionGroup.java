
import java.util.ArrayList;


public class ConnectionGroup {
	
    public ArrayList<User> group;
    
    public ConnectionGroup(ArrayList<User> group){
    	this.group=group;
    }

    
    public ArrayList<User> getGroup() {
		return group;
	}

	public void setGroup(ArrayList<User> group) {
		this.group = group;
	}

	public ConnectionGroup()
    {
    	group = new ArrayList<User>();
//    	userNumber = 0;
    }
    
    public void addUser(User user)
    {
    	group.add(user);
//    	userNumber++;
    }
    
    public void deleteUser(User user)
    {
    	group.remove(user);
//    	userNumber--;
    }
    public void deleteUserNew(String name)
    {
    	int a = 0;
    	for(int i =0; i < group.size(); i++)
    	{
    		if(group.get(i).getUserName().equals(name))
    		{
    			a = i;
    		}
    	}
    	group.remove(a);
    }

	@Override
	public String toString() {
		String groupString = group.get(0).toString();
		for(int i = 1; i < group.size(); i++)
		{
			groupString = groupString + "|" + group.get(i).toString();
		}
		
		
		return groupString.toString();
	}

//	public int getUserNumber() {
//		return userNumber;
//	}
//
//	public void setUserNumber(int userNumber) {
//		this.userNumber = userNumber;
//	}
}
