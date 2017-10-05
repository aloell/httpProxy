package httpProxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
public class MySocketHandlerNG extends Thread
{
		Socket browserSocket;
		int threadNumber;
		LinkedList<DNSrecord> DNScache;
		Socket toOriginSocket;
		InputStream fromOrigin;
		OutputStream toClient;
		byte[] buffer;
		int indicatorE=0;
		OutputStream toOrigin;
		PrintWriter bufferPush;
		public  MySocketHandlerNG (Socket s, int i,LinkedList<DNSrecord> DNScache)
		{
			this.browserSocket=s;
			threadNumber=i;
			this.DNScache=DNScache;
		}
		public void run()
		{
			
			try
			{
					String destination=new String();
					int index=0;
					String originFirst=new String();
					int checkFirst=0;
					//first part is to read request line and headers into an array list
					InputStream fromClient=browserSocket.getInputStream();
					Scanner bufferin=new Scanner(fromClient);
					//while(bufferin.hasNextLine())
					//{
					ArrayList<String> headerBuffer=new ArrayList<String>();
					index=0;
					while(bufferin.hasNextLine())
					{
						destination=bufferin.nextLine();
						//System.out.println("!!!ThreadNumber:"+ threadNumber);
						//System.out.println("!!!checkFirst:"+ checkFirst);
						//System.out.println(destination);
						//blank line stands for the end of request
						if(destination.equals(""))
						{
							break;
						}
						if(index==0)
						{
							originFirst=destination;
							System.out.println("ThreadNumber"+threadNumber);
							System.out.println(originFirst);
							index++;
							continue;
						}
						headerBuffer.add(destination);
						index++;
					}
					
					//the second part is to parse the request line
					// i only accept the GET command, other request line will be
					// regarded as the GET request to www.case.edu
					String HostName;
					String relativePath;
					if(originFirst.startsWith("GET")&&originFirst.contains("HTTP"))
					{
						String[] requestLine=originFirst.split("\\s");
						String wholeURL=requestLine[1];
						String name=wholeURL.substring(7);
						int endOfHost=name.indexOf('/');
						HostName=name.substring(0,endOfHost);
						//System.out.println("HostName: "+HostName);
						relativePath=name.substring(endOfHost);
					}
					else
					{
						HostName="www.case.edu";
						relativePath="/";
					}
				
					//the third part is to check if there is ip address for the host name
					// in the cache already, if not, ask for it, and store it into the cache
					InetAddress ipAddr=null;
					synchronized(DNScache)
					{
						int findDNS=0;
						for(DNSrecord dr:DNScache)
						{
							if(dr.hostName.equals(HostName))
							{
								System.out.println(dr);
								ipAddr=dr.correspondIP;
								findDNS=1;
								break;
							}
						}
						if(findDNS==0)
						{
							ipAddr=InetAddress.getByName(HostName);
							DNSrecord dnsCell=new DNSrecord(HostName,ipAddr);
							DNScache.add(dnsCell);
							//this thread is responsible for deleting the record after 30 seconds
							autoDeleteDNS timerForDNS=new autoDeleteDNS(DNScache,dnsCell);
							timerForDNS.start();
						}
					}
					
					//the fourth part is to establish TCP connection with the host
					//also send the parsed request line and header lines out
					//if(checkFirst==0)
					//{
						toOriginSocket=new Socket(ipAddr,80);
						toOrigin=toOriginSocket.getOutputStream();
						bufferPush=new PrintWriter(toOrigin,true);
					//}
					String firstLine="GET "+relativePath+" HTTP/1.1";
					System.out.println("ThreadNumber: "+threadNumber);
					System.out.println(firstLine);
					bufferPush.println(firstLine);
					for(String s:headerBuffer)
					{
						//System.out.println(s);
						bufferPush.println(s);
					}
					//System.out.println(threadNumber);
					//System.out.println();
					bufferPush.println();

					//the last part is to fetch the object back from the server to the browser
					//if(checkFirst==0)
					//{
						fromOrigin=toOriginSocket.getInputStream();
						toClient=browserSocket.getOutputStream();
						buffer=new byte[1024];
						indicatorE=0;
					//}
					
					while((indicatorE=fromOrigin.read(buffer,0,1024))!=-1)
					{
						//System.out.println(indicatorE);
						toClient.write(buffer,0,indicatorE);
						
					}
					
					
					fromOrigin.close();
					toClient.close();
					bufferPush.close();
					bufferin.close();
					toOriginSocket.close();
					browserSocket.close();
					
					
					System.out.println("ThreadNumber: "+threadNumber+" checkNumber: "+checkFirst+" One object fetched successfully!");
					/*while(bufferin.hasNextLine())
					{
						System.out.println("Afterwards Threadnumber:"+threadNumber);
						System.out.println(bufferin.nextLine());
					}*/
					//checkFirst++;
					//}
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
			
	}
}