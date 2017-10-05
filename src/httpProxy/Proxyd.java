package httpProxy;

import java.io.*;
import java.util.*;
import java.net.*;

public class Proxyd
{
	public static void main(String[] args) 
	{
		int MyPort=5505;
		if(args.length==2)
		{
			MyPort=Integer.parseInt(args[1]);
			System.out.println("Port number is: "+MyPort);
		}
		try
		{
			ServerSocket s=new ServerSocket(MyPort);
			int i=0;
			LinkedList<DNSrecord> DNScache=new LinkedList();
			while(true)
			{
				Socket incoming=s.accept();
				//i stands for the thread number
				//new thread is created for fetching web objects
				i++;
				Thread r1=new MySocketHandlerNG(incoming,i,DNScache);
				r1.start();
			}
			
		}
		catch(IOException e)
		{
			System.out.println("problem even accept!");
			System.out.println(e);
		}
	}
}