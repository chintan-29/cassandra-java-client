=================================
monitor-agent project
=================================
the main target for this project is provide a agent for other automation
monitor tool talk with Cassandra.

By default, Cassandra support rich JMX monitor point, this agent will export
these JMX properties to SNMP or HTTP Restful call.

In long term, agent will provide a dashborad/console by HTTP, user can easile 
do operation in the console.


==================================
JMX-Console
==================================
this JMX-Console is a simple, read only JMX console application, fully implment 
with GWT2.0. We can using this console to got fully current running status of 
Cassandra server via HTTP/Browser.
