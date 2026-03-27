# ePAS - UGov PJ UWeb  Timesheet
## ePAS - UGov PJ UWeb Timesheet - Integration service

[![Supported JVM Versions](https://img.shields.io/badge/JVM-25-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.java.net/install/)

*ePAS - UGov PJ UWeb Timesheet* è il servizio che permette di esportare le informazioni presenti in
[ePAS](https://epas.projects.iit.cnr.it) nelle modalità e formato previsti dal sistema di rendicontazione dei timesheet di Cineca denominato *UGov PJ UWeb Timesheet Intime*.

In particolare permette di esportare le informazioni sul tempo a lavoro e le assenze del personale
presente in ePAS in una finestra di tempo configurabile.
Queste informazioni su tempo a lavoro e assenze sono poi utilizzate dall'applicativo Cineca per
guidare gli utenti nella compilazione dei timesheet tenendo in considerazione vincoli e
suggerimenti che derivano da questi dati.

Il software è distribuito come immagine [Docker](https://docker.com) 
configurata per accedere a scadenza regolare via REST alle informazioni di ePAS ed inserirle
nelle tabelle di frontiera Oracle di UGov Pj UWeb Timesheet Intime.

## Modalità di integrazione

UGov PJ UWeb Timesheet prevede la possibilità di inserire in una tabella di frontiera *Oracle* le
informazioni relative a tempo a lavoro e assenze che vengono prelevate via REST da un server ePAS.

All'interno di ePAS è necessario create un utente che abbia il ruolo di sistema
*Consultatore riepiloghi orari e assenze*, le credenziali di questo utente devono essere poi
specificate nella configurazione di questo servizio (*epas.username*, *epas.password*).


## Configurazione di ePAS per il mapping delle tipologie di assenza

UGov Pj Timesheet prevede di raggruppare le assenze in alcune tipologie, ma nel caso 
del CNR è stato deciso di utilizzare solo queste due tipologie:

 - *T* - Missione / Trasferta
 - *X* - Assenze

Per questo motivo in ePAS è stata aggiunta la possibilità per ogni codice di assenza di specificare
un campo **External Id**, questo campo deve essere valorizzata per i tipi di assenza che si vogliono
esportare verso il sistema Cineca con il rispettivo codice Cineca.
 
Per caso del CNR tutte le assenze sono considerate di tipo *X*, mentre per determinare quelle di tipo
Missione/Trasferta è necessario impostare in ePAS il campo *External Id* con il valore *T* nei codici
di assenza per missioni, come per esempio i codici *92*, *92M*, *92E* e *92RE*.

Le assenze di tipo *X* verranno esportate comprensive di una etichetta (del tipo "codice - descrizione codice")
che permetterà di identificare l'assenza.
 
## Configurazione e avvio del servizio tramite docker/docker-compose

ePAS UGov Pj Timesheet può essere facilmente installato via docker-compose su server Linux
utilizzando il file docker-compose.yml ed il .env presenti in questo repository.

Per l'accesso al database Oracle è necessario richiedere le informazioni di accesso a Cineca.
In particolare è necessario avere il **TNS Oracle**, il nome dello schema SQL, username 
e password per l'accesso al db. 
Inoltre Cineca tipicamente fornisce l'accesso al database Oracle solo tramite VPN, di cui
eventualmente fare richiesta a Cineca.

Accertati di aver installato docker e docker-compose dove vuoi installare *ePAS UGov Pj Timesheet*
ed in seguito scarica il [docker-compose.yml](docker-compose.yml) ed il [.env](.env) di esempio.

```
curl --remote-name-all https://raw.githubusercontent.com/consiglionazionaledellericerche/epas-ugov-pj-timesheet/main/{compose.yaml,VERSION}
```

Tipicamente non è necessario modificare il docker-compose.yml, mentre è obbligatorio
inserire nel [.env](.env) i parametri per la connessione al db Oracle e per l'accesso all'API REST
di ePAS. 
Per la configurazione del file [.env](.env) è possibile seguire i commenti già presenti.

Una volta configurati i parametri del [.env](.env) è possibile avviare il servizio nella modalità
standard docker-compose:

```
docker-compose up -d
```

## Utilizzo della VPN Cisco su Linux

Utilizzando su Ubuntu il client VPN Cisco Anyconnect ci possono essere dei problemi di routing
che non permettono al container docker di raggiungere gli host del database Oracle tramite la VPN.
Per lo sviluppo di questo servizio è stato utilizzato su Ubuntu il client VPN **openconnect**.

```
sudo apt install openconnect network-manager-openconnect network-manager-openconnect-gnome
```

e configurando la VPN tramite VPN Settings -> Multi-protocol VPN client (open connect).

## Endpoint REST del servizio

Questo servizio integra alcuni endpoint REST di amministrazione che permettono di lanciare su
richiesta nuove sincronizzazioni dei dati, oltre a vari task di amministrazione.
Gli endpoint REST sono protetti tramite Basic Auth, con utente e password configurato tramite
le application.properties del servizio, oppure tramite le variabili d'ambiente 
*spring.security.username*, *spring.security.password* nel caso di avvio tramite docker/docker-compose.

## Task schedulati ogni giorno e loro consultazione

È possibile visualizzare le informazioni dei task cron schedulati per la sincronizzazone
delle informazioni tra ePAS e le tabelle Oracle di UGOV PJ UWeb Timesheet.

L'endpoint da consultare è **/actuator/scheduledtasks**.

**Il servizio effettua l'aggiornamento dei dati di presenze ed assenze di tutto il personale oggi mattina alle 05:30 AM.**

## OpenAPI ed interfaccia Swagger integrata

Il servizio espone le informazioni sulle proprie API REST in formato *OpenApi* all'indirizzo **/v3/api-docs**.
Inoltre è presente un'interfaccia web *Swagger UI* con la descrizione di tutti i servizi REST e la possibilità di effettuare le chiamate REST, l'interfaccia è raggiungibile all'indirizzo **/swagger-ui/index.html**. 

## Metriche del servizio

Il servizio esporta alcune metriche in formato Prometheus, è possibile consultarle all'endpoint
**/actuator/prometheus**.

Le metriche presenti contengono anche le tempistiche di sincronizzazione di tutti i dati e dei 
singoli uffici, del tipo:

```
# HELP epas_sync_office_month_seconds Timer della sincronizzazione dei dati di un mese di un ufficio
# TYPE epas_sync_office_month_seconds summary
epas_sync_office_month_seconds_count{officeId="2",yearMonth="2024-01",} 1.0
epas_sync_office_month_seconds_sum{officeId="2",yearMonth="2024-01",} 0.237913661
epas_sync_office_month_seconds_count{officeId="2",yearMonth="2023-12",} 1.0
epas_sync_office_month_seconds_sum{officeId="2",yearMonth="2023-12",} 0.287619208
epas_sync_office_month_seconds_count{officeId="1",yearMonth="2024-02",} 1.0

# HELP epas_sync_all_time_seconds Time taken to sync all the details
# TYPE epas_sync_all_time_seconds summary
epas_sync_all_time_seconds_count{class="it.cnr.iit.epas.timesheet.ugovpj.service.SyncService",exception="none",method="syncAll",} 1.0
epas_sync_all_time_seconds_sum{class="it.cnr.iit.epas.timesheet.ugovpj.service.SyncService",exception="none",method="syncAll",} 6.734464188
```

## 👏 Come Contribuire 

E' possibile contribuire a questo progetto utilizzando le modalità standard della comunità opensource 
(issue + pull request) e siamo grati alla comunità per ogni contribuito a correggere bug e miglioramenti.

## 📄 Licenza

ePAS - UGov PJ UWeb  Timesheet è concesso in licenza GNU AFFERO GENERAL PUBLIC LICENSE, come si trova 
nel file [LICENSE][l].

[l]: https://github.com/consiglionazionaledellericerche/epas-ugov-pj-timesheet/blob/master/LICENSE