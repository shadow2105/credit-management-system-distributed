package com.example.cmscreditstatementservice.listener.deserializer;

import com.example.cmscreditstatementservice.domain.CreditAccount;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.TimeZone;
import java.util.UUID;

// https://debezium.io/documentation/reference/2.6/connectors/mysql.html#mysql-data-types
public class CreditAccountDeserializer extends StdDeserializer<CreditAccount> {
    protected CreditAccountDeserializer() {
        this(null);
    }

    protected CreditAccountDeserializer(Class vc) {
        super(vc);
    }

    @Override
    public CreditAccount deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        /*{
            "type": "bytes",
            "optional": false,
            "field": "id"
          }*/
        // https://stackoverflow.com/questions/24408984/convert-bytearray-to-uuid-java
        byte[] idBytes = Base64.getDecoder().decode(node.get("id").asText());
        UUID id = getGuidFromByteArray(idBytes);

        /*{
            "type": "int64",
            "optional": true,
            "name": "io.debezium.time.MicroTimestamp",
            "version": 1,
            "field": "created_at"
          }*/
        // https://github.com/debezium/debezium/blob/main/debezium-core/src/main/java/io/debezium/time/MicroTimestamp.java
        long createdAtMillis = node.get("created_at").asLong() / 1000;
        Timestamp createdAt = new Timestamp(createdAtMillis);

        // createdAtMillis is the value captured from the database (stored in System Timezone) converted to milliseconds
        // new Timestamp(millis) expects a value in milliseconds in GMT/UTC
        // new Timestamp(createdAtMillis) has an offset of TimeZone.getDefault().getOffset(createdAt.getTime())
        int systemTimezoneOffsetMillis = TimeZone.getDefault().getOffset(createdAt.getTime());
        createdAt.setTime(createdAt.getTime() - systemTimezoneOffsetMillis);

        /*{
            "type": "int64",
            "optional": true,
            "name": "io.debezium.time.MicroTimestamp",
            "version": 1,
            "field": "updated_at"
          }*/
        long updatedAtMillis = node.get("updated_at").asLong() / 1000;
        Timestamp updatedAt = null;
        if (updatedAtMillis != 0) {
            updatedAt = new Timestamp(updatedAtMillis);

            updatedAt.setTime(updatedAt.getTime() - systemTimezoneOffsetMillis);
        }

        /*{
            "type": "string",
            "optional": true,
            "field": "created_by"
          }*/
        String createdBy = node.get("created_by").asText();

        /*{
            "type": "string",
            "optional": true,
            "field": "updated_by"
          }*/
        String updatedBy = node.get("updated_by").asText();

        /*{
            "type": "string",
            "optional": false,
            "field": "customer_id"
          }*/
        String customerId = node.get("customer_id").asText();

        /*{
            "type": "string",
            "optional": false,
            "field": "account_number"
          }*/
        String accountNumber = node.get("account_number").asText();

        /*{
            "type": "bytes",
            "optional": false,
            "name": "org.apache.kafka.connect.data.Decimal",
            "version": 1,
            "parameters": {
              "scale": "2",
              "connect.decimal.precision": "38"
            },
            "field": "credit_limit"
          }*/
        // https://debezium.io/documentation/faq/#how_to_retrieve_decimal_field_from_binary_representation
        BigDecimal creditLimit = decodeDecimal(node.get("credit_limit").asText());

        /*{
            "type": "bytes",
            "optional": false,
            "name": "org.apache.kafka.connect.data.Decimal",
            "version": 1,
            "parameters": {
              "scale": "2",
              "connect.decimal.precision": "38"
            },
            "field": "current_balance"
          }*/
        BigDecimal currentBalance = decodeDecimal(node.get("current_balance").asText());

        /*{
            "type": "bytes",
            "optional": false,
            "name": "org.apache.kafka.connect.data.Decimal",
            "version": 1,
            "parameters": {
              "scale": "2",
              "connect.decimal.precision": "38"
            },
            "field": "interest_rate"
          }*/
        BigDecimal interestRate = decodeDecimal(node.get("interest_rate").asText());

        CreditAccount creditAccount = new CreditAccount(customerId, accountNumber, creditLimit, currentBalance,
                interestRate);

        creditAccount.setId(id);
        creditAccount.setCreatedAt(createdAt);
        creditAccount.setUpdatedAt(updatedAt);
        creditAccount.setCreatedBy(createdBy);
        creditAccount.setUpdatedBy(updatedBy);

        return creditAccount;
    }

    private static UUID getGuidFromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low);
    }

    private BigDecimal decodeDecimal(String base64EncodedValue) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedValue);
        BigInteger encodedValue = new BigInteger(decodedBytes);
        // The scale of the value should be known from the schema or obtained in advance
        int scale = 2; // Assuming scale is known to be 2
        return new BigDecimal(encodedValue, scale);
    }
}