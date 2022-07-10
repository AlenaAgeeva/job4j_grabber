<h1 align="left">Grabber Project</h1>
<h3 align="left">The program aggregates all vaccines from a website and put to a database</h3>
<p align="left">The system starts according to the schedule - once a minute.</p>
<p align="left">The launch period is specified in the settings - app.properties.</p>
<p align="left">The first site will be career.habr.com.</p>
<p align="left">It has a section https://career.habr.com/vacancies/java_developer.</p>
<p align="left">The program will work with information from this.</p>
<p align="left">The program should read all vacancies related to Java and write them to the database.</p>
<p align="left">Access to the interface will be via the REST API.</p>
<h4 align="left">Updates</h4>
<p align="left">1. New sites can be added to the project without changing the code.</p>
<p align="left">2. In the project, you can do parallel parsing of sites.</p>
<h2 align="left">Used technologies:</h2>
<ul><li>JSOUP for parsing</li>
<li>JDBC for manipulating with DB</li>
<li>Quartz library for scheduling</li>
<li>Slf4j for logging</li>
</p>
<p align="left">
  <img src="https://user-images.githubusercontent.com/67174823/178151771-4a04984f-3f98-4395-ac19-41d3741e017e.jpg" width="85%">
  <img src="https://user-images.githubusercontent.com/67174823/178151823-a0dad56e-a748-4f9d-b9a7-c01254504f81.jpg" width="85%">
</p>
