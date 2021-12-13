package com.newrelic.logging.core;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class IPResolveHelper
{
    private static String machineIp;

    public static String getMachineIp()
    {
        if (machineIp == null)
        {
            try (final DatagramSocket socket = new DatagramSocket())
            {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                machineIp = socket.getLocalAddress().getHostAddress();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                machineIp = "RESOLVE_IP_ERROR";
            }
        }
        return machineIp;
    }
}