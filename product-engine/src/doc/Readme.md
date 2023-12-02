# Сервис Product Engine

Отвечает за создание договоров и графиков платежей. Использует систему версионирования БД Liquibase. Имеет две gRPC ручки:<br>
1. <b>CreateAgreement:</b> <br>
    Входные данные: <br>
   &emsp;int32 client_id = 1; - идентификатор клиента<br>
   &emsp;int32 loan_term = 2; - срок кредита (месяцы)<br>
   &emsp;double disbursement_amount = 3; - сумма кредита<br>
   &emsp;double interest = 4; - ставка (0.0 - 1.0) <br>
   &emsp;string product_code = 5; - код продукта (тип кредита) <br>
    Ответ: <br>
   &emsp;int64 agreement_id = 1; - идентификатор созданного договора
2. <b>CreateDisbursement:</b> <br>
    Входные данные: <br>
   &emsp;string disbursement_date = 1; - дата выплаты денег клиенту<br>
   &emsp;int64 agreement_id = 2; - идентификатор договора<br>
   Ответ: <br>
   &emsp;int64 payment_schedule_id = 1; - идентификатор созданного расписания платежей

### Схема Базы Данных 
![Database_Schema.jpg](Database_Schema.jpg)