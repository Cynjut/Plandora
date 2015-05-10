# Plandora
Plandora Project Management System

This is a reboot of Plandora, the now 10 year old Project Planning and Management suite. Make no mistake - this project is based on the excellent work of the previous Plandora team and we wouldn't be able to manage what we hope will become an improved system without their work and dedication. Having said that, with the original author MIA and unable to answer questions, I needed a place to put the source code so that I could fix problems as I find them.

The first change was to correctly spell the word "requirement" everyplace it appears. After that, all bets are off as to what will get fixed next.

A few things that I've already fixed are documented below.

My big impetus for this is actually documentation, which I hope to start working on shortly. If there is a single problem with Plandora, it's the rather resounding lack of anything but design documentation. For example, the Workflow wizard (which in itself appears to be an awesome addition) doesn't have anything like code reviews or other milestones. There are also several pieces that really need to get written from a 'getting started' perspective, including all of the simple forms for updating functions, areas, and other components. 

In addition to the internals of the system, we are also looking at providing integration of "RT4" with the system, allowing existing RT installations to use Plandora as a mechanism for tracking and monitoring projects based on trouble ticketing. This will simplify life for many people, since the public face of the system for "non-resources" will now be external to the development process.

Version 1.13.1

==> Fixes
- replaced all occurrences of the word 'requeriment' with 'requirement'. Sorry, it was driving me nuts.
- added (documented) support for Java version 8. The system now builds correctly in Eclipse and runs under the Java 1.8 JRE and JDK. Special note - I hoisted myself on my own petard here; I don't have a NetBSD server that runs the 1.8 JRE yet.


From the Version 1.13.0 README

# Features

- Balanced Scorecard of Projects
- Meta-fields for main forms
- Gantt Chart
- Knowledge Base with 'google-like' searching
- Customized Reports using JasperReports
- TO-Do Lists for project resources
- Workflow
- Capacity Management
- Custom Project Surveys
- Integration with SVN
- Agile Board built-in
- Project Risks monitoring and management


==> Bug
- bug fix at CSV export from grid data. There was a bug when a cell contained values with 'line feed'. The csv was formated wrongly;
- bug fix at Survey Form. There was a bug at 'paging' link. An undesired report was popup after each re-paging;
- bug fix at popup form of Link Relation feature. The parent form was reloading in a blank page;
- bug fix at iteration combo of 'All Requests' form;
- bug fix at project form. The combo status was populated only with 'close' option after closing an arbitrary project and pressing the clear button (thanks to Tiago Picon);
- bug fix at category form. The 'remove category' option was not working (thanks to Tiago Picon);
- bug fix at relationship grid of Task Form (thanks to Juan Otth);
- bug fix at used capacity calculation at Resource Capacity Form (thanks to Eduardo Milanez);
- bug fix at "My Teams" DB query. There was a bug when the resource member was desalocated of all projects;
- after executing a relationship action (task, occurrence, etc) the system creates a new link but shows a error message: "A relationship between this two entities already exists". The bug was fixed;
- bug fix at MSProject import. The task name must be trucated. (thanks to Juan Otth);
- gantt chart was showing an empty macro task if a resource members was selected (filter) and the macro task didn't contain tasks into it;
- bug fix at Gantt Project exporting. The task name formating was not working appropriately. (thanks to Rocío E. Villalba);
- bug fix at meta form grid object (adding a row);
- bug fix at Task form. The 'is macro task' check box was saving with enabled status even when user set to disabled. 
- bug fix at expenses form. A expense item was editable even after changed to paid or cancelled. (thanks to Paola Tame);
- the form of cost, invoices and expenses were not handling appropriately big values (thanks to Juan Otth);

==> Feature
- an attribute 'is_mandatory' was included at meta_field. Now it is possible to define which meta_field have to be fill-in by user;
- now the gantt chart shows the calendar events and milestones;
- implementation of new agent to import JIRA issues to Plandora requests (including attachments);
- adjust at workflow popup. Now it is displayed the description of related request and the follow-up of all comments typed by resorces during workflow life-cycle;
- now the "Show All Tasks" form contains a filter by date (last updating task date);
- implementation of procedure to import requests from CSV file;
- now the company form contains new fields: address, city, state, etc;
- performance tunning at 'Show All Tasks' and 'Show All Requests' Forms;
- implementation of new agent "Report2File" to schedule a report execution and save at filesystem;
- now, the report form and report2email agent can generate a Plandora report in JPG format;
- now, the resource capacity form contains a filter to shows a comparison between estimated capacity and used capacity. Besides, it is possible to filter the resource capacity panel by zero/empty values;
- now, the metafields of: task, requests and projects could be displayed at Main Form grids;
- now, the request ID of a task is a editable field at 'All Tasks Form';
- now, it is possible filter the 'Iteration Burndown Gadget' chart by project category;
- the 'removeItem' method was included at repository interface. The features was implemented only to DB Repository;
- it was included the field 'smtp port' at the email notification agents;
- now, the Plandora is able to open a JDBC connection directly ('bypassing' Tomcat connection polling). The default behaviour uses the Tomcat connection polling. Check web.xml file after Plandora deploy;
- implementation of EDI form to export user data to RSS and ICAL formats.
