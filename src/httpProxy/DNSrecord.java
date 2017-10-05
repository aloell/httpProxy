package httpProxy;

import java.net.InetAddress;

public class DNSrecord {
	//this is the format for every cell in the DNS cache
	//host name and corresponding ip address
	String hostName;
	InetAddress correspondIP;
	public DNSrecord(String hostName, InetAddress correspondIP)
	{
		this.hostName=hostName;
		this.correspondIP=correspondIP;
	}
	//i define the equal is the host name is same
	public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof DNSrecord) {
            DNSrecord anotherString = (DNSrecord)anObject;
            if(this.hostName.equals(((DNSrecord) anObject).hostName))
            		return true;
        }
        return false;
    }
}