package payment;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class BLOBTest {

	public static void main(String[] args) throws Exception{
        System.out.println("kshitij");
        Class.forName("com.mysql.jdbc.Driver");
        Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/jsfdb","root","mouqi123");
       

        File f1=new File("/home/mackie/shell/aaa.tar.gz");
        FileInputStream fin=new FileInputStream(f1);
        PreparedStatement pst = cn.prepareStatement("insert into registration(image) values(?)");
        //pst.setInt(1,67);
        pst.setBlob(1, fin);
        pst.executeUpdate();

        fin.close();
        System.out.println("Quesry Executed Successfully");
	}
}
