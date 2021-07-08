# TIW exam project

**Table of contents**
- [Description](#description)
- [Software requirements](#software-requirements)
- [Running](#running)
  - [Notes](#notes)   
- [Contributors](#contributors)
- [Evaluation](#evaluation)

## Description
This repository contains all the material used for the contributors' TIW exam in 2021 at [Politecnico di Milano](https://www.polimi.it).

You can find the project task [here](https://github.com/gblfrc/tiw-2021-gibellini-montemurro/blob/main/deliverables/specifica_progetto_aggiornata.pdf).

## Software requirements
The project was produced using ***Eclipse (Java Enterprise Edition)***, which provides a built-in facet for web projects, but any IDE with the same functionality should work as fine. In addition, a server is needed to run the project and let it be usable by whichever browser; for this purpose, ***Apache Tomcat 9.0*** was used.
Also, a database software is required; the project was developed using ***MySQL***. In that case, only ***MySQL Workbench***, ***MySQL Server*** and ***MySQL J connector*** are strictly necessary.

## Running
To run the project:
- Import it on your IDE cloning this repository
- Import the project's database [dump](https://github.com/gblfrc/tiw-2021-gibellini-montemurro/blob/main/deliverables/tiw_project.sql) on your DB
- Update web.xml with your personal DB information (DB schema's name, username, password)
- Add the project to the server
- Start the server
- Open a browser and head to the project's main page.

#### Notes

- If the project is running on localhost the project's path should be: 

  ` localhost:8080/tiw-2021-gibellini-montemurro/ `

  Remember to replace 8080 with the actual number of the port assigned to the server.

- Make sure all of the software is connected together, e.g. including MySQL Java connector in the project's build path.
- If server throws exception while starting, check to have the right timezone set on DB software

## Contributors
- [Gibellini Federico](https://github.com/gblfrc)
- [Montemurro Elena](https://github.com/ElenaMontemurro)

## Evaluation
This project was awarded a 30/30 grade at the exam.
