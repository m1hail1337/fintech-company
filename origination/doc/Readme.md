# Сервис Origination

Отвечает за обработку входящих заявок на получение кредита или ее закрытие. Использует систему версионирования БД Liquibase. Имеет две gRPC ручки:<br>
1. <b>CreateApplication:</b> <br>
   Входные данные: <br>
   &emsp;string first_name = 1; - имя клиента<br>
   &emsp;string last_name = 2; - фамилия клиента<br>
   &emsp;string email = 3; - эл. почта клиента<br>
   &emsp;uint32 salary = 4; - зарплата клиента <br>
   &emsp;uint32 disbursement_amount = 5; - запрашиваемое кол-во денег <br>
   Ответ: <br>
   &emsp;string application_id = 1; - идентификатор созданной заявки
2. <b>CancelApplication:</b> <br>
   Входные данные: <br>
   &emsp;string application_id = 1; - идентификатор заявки для отмены<br>
   Ответ: <br>
   &emsp;bool is_canceled = 1; - результат отмены заявки (да/нет - true/false) 

### Схема Базы Данных
![Origination_Database_Schema.jpg](Origination_Database_Schema.jpg)