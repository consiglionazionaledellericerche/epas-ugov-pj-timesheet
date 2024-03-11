# ePAS - UGov PJ UWeb  Timesheet
## ePAS - UGov PJ UWeb Timesheet - Integration service

[![Supported JVM Versions](https://img.shields.io/badge/JVM-17-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.java.net/install/)

*ePAS - UGov PJ UWeb Timesheet* è il servizio che permette di esportare le informazioni presenti in ePAS
nelle modalità e formato previsti dal sistema di rendicontazione dei timesheet di Cineca denominato *UGov PJ UWeb Timesheet*.
In particolare permette di esportare le informazioni sul tempo a lavoro e le assenze del personale presente in ePAS in una
finestra di tempo configurabile.
Queste informazioni su tempo a lavoro e assenze sono poi utilizzate dall'applicativo Cineca per guidare gli utenti nella
compilazione dei timesheet tenendo in considerazione vincoli e suggerimenti che derivano da questi dati.

## Modalità di integrazione

UGov PJ UWeb Timesheet prevede la possibilità di inserire in una tabella di frontiera *Oracle* le informazioni relative a 
tempo a lavoro e assenze che vengono prelevate via REST da un server ePAS.

## Endpoint REST del servizio

Questo servizio integra alcuni endpoint REST di amministrazione che permettono di lanciare su richiesta
nuova richieste di sincronizzazione dei dati, oltre a vari task di amministrazione.
Gli endpoint REST sono protetti tramite Basic Auth, con utente e password configurato tramite
le application.properties del servizio, oppure tramite le variabili d'ambiente 
security.username,security.password nel caso di avvio tramite docker/docker-compose. 

# Visualizzazione task schedulati

È possibile visualizzare le informazioni dei task cron schedulati per la sincronizzazone
delle informazioni tra ePAS e le tabelle Oracle di UGOV PJ UWeb Timesheet.

L'endpoint da consultare è **/actuator/scheduledtasks**.

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