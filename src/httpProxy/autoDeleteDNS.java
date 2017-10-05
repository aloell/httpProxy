package httpProxy;

import java.util.LinkedList;

public class autoDeleteDNS extends Thread{
	//DNS cache is implemented as an linked list
	LinkedList<DNSrecord> DNScache;
	DNSrecord targetRecord;
	public autoDeleteDNS(LinkedList<DNSrecord> DNScache,DNSrecord ds)
	{
		this.DNScache=DNScache;
		this.targetRecord=ds;
	}
	//this thread is invoked by thread MySocketHandlerNG.
	//its task is to delete the DNS record after it has been 
	//put into the cache 30 seconds
	public void run()
	{
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized(DNScache)
		{
			DNScache.remove(targetRecord);
		}
	}
}
