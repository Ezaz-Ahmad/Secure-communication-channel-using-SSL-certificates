#!/usr/bin/env python3
"""
This program was written exclusively for educational purposes for the System and Network Security (SENG2250)
course taught at the University of Newcastle, Australia. On using this program, you agree to not modify it
for illicit or immoral purposes.

This an interceptor adversary, make sure to first install the scapy package with for example:
pip install scapy
Where the above line is input directly into a command prompt/terminal.

If you are on a linux or mac system, then you will probably have to run this program with root
privileges, e.g., sudo python3 interceptor.py
"""

from scapy.all import sniff, Raw, TCP


def print_tcp_payload(pkt):
    "Print the payload data from inside a TCP packet if it has one"
    if isinstance(pkt[TCP].payload, Raw):
        print("Captured a packet, here are the contents:")
        try:
            print(bytes.decode(pkt[TCP].payload.load))
        except UnicodeDecodeError:
            print("The victim's packets are encrypted, the message appears to be random bytes.")
        print()


if __name__ == "__main__":
    print("Starting interception of local traffic, now start the server then client.")
    # You may need to change the iface option, depending on your computer's setup
    # Sometimes remove the `iface='lo'` part altogether works better
    capture = sniff(iface='lo', filter="port 2250 and host 127.0.0.1", prn=print_tcp_payload, count=20)