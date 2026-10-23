PublicHospitalInfoManager

A queue and appointment system for public hospitals.

Overview

• Check-in is done at kiosks since some patients might have no phone or smartphone  
• Patients choose walk-in, appointment or emergency  
• Patients enter their full name and ID number  
• Patients with no identification due to being unconscious can have enter a code word instead (only known by nurses to bypass ID request) 
• Each patient gets a ticket number and a file number (only for new client) previous clients gets matched on the admin side
• Linking files to ID numbers helps nurses find the right file quickly  

Check-in kiosk

• Choose walk-in, appointment or emergency  
• Enter full name  
• Enter ID number or create a code word if there is no identification  
• Receive a ticket number, a file number and the number of people ahead in the queue  
• Emergency patients are seen first, then appointments, then walk-ins  

Unconscious or unidentified patients

• A nurse registers the patient with a temporary code word  
• Treatment and record keeping can start straight away  
• When the ID is found or the patient wakes up, the record is linked to the ID number  
• Nurses can then open the full file using the ID number  

Staff dashboard

• View the live queue  
• Call the next patient to a room  
• Mark a visit as done and add notes  
• Search for a patient file by ID number or code word  

Doctors

• Book appointments for patients  
• View and cancel upcoming appointments  
• Patients with an appointment check in at the kiosk under the appointment category  

Patient app(optional)

• View upcoming appointments  
• View past visits  
• Chronic patients can set medication reminders with the medicine name, dosage and time  

Tech stack

• Java 21  
• Spring Boot  
• REST API  
• H2 database for development  
• PostgreSQL for production  

Requirements

• Java 21 or newer  
• Maven  

How to run

• Clone the repository from https://github.com/IdahRaseroka/PublicHospitalInfoManager  
• Open the project folder  
• Run mvnw spring-boot:run  
• Open http://localhost:8080 in your browser  

Project status

• Early development  
• Check-in kiosk, queue and appointments are being built first  
• Patient mobile app is planned for a later stage  

Future plans

• Staff login for admin, nurse and doctor  
• SMS appointment reminders for patients without smartphones  
• Mobile app with push notifications for medication reminders  
• Waiting room screen showing which ticket is being called  
• Fingerprint check-in for patients without identification  


- Upload the saved file directly from `C:\Users\koket\HospitalTicketSystem\README.md`.
- Name the file `README.txt`. GitHub then shows it exactly as written, with no formatting changes.
