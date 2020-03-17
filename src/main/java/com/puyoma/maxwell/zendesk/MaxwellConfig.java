package com.puyoma.maxwell.zendesk;

import com.github.shyiko.mysql.binlog.network.SSLMode;
import com.google.common.base.Strings;
import com.zendesk.maxwell.MaxwellMysqlConfig;
import com.zendesk.maxwell.filtering.Filter;
import com.zendesk.maxwell.filtering.InvalidFilterException;
import com.zendesk.maxwell.monitoring.MaxwellDiagnosticContext;
import com.zendesk.maxwell.producer.EncryptionMode;
import com.zendesk.maxwell.replication.BinlogPosition;
import com.zendesk.maxwell.replication.Position;
import com.zendesk.maxwell.scripting.Scripting;
import joptsimple.OptionSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.zendesk
 * @date:2020/3/11
 */
public class MaxwellConfig extends com.zendesk.maxwell.MaxwellConfig {
    static final Logger LOGGER = LoggerFactory.getLogger(MaxwellConfig.class);

    public MaxwellConfig(String argv[],String source) throws Exception {
        super();
        this.parse(argv,source);
    }


    private void parse(String [] argv,String source) throws IOException {
        OptionSet options = buildOptionParser().parse(argv);

        Properties properties;

        if(!Strings.isNullOrEmpty(source)){
            properties =  new Properties();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(source.getBytes("UTF-8"));
            properties.load(inputStream);

        }else {
            if (options.has("config")) {
                properties = parseFile((String) options.valueOf("config"), true);
            } else {
                properties = parseFile(DEFAULT_CONFIG_FILE, false);
            }
        }

        String envConfigPrefix = fetchOption("env_config_prefix", options, properties, null);

        if (envConfigPrefix != null) {
            String prefix = envConfigPrefix.toLowerCase();
            System.getenv().entrySet().stream()
                    .filter(map -> map.getKey().toLowerCase().startsWith(prefix))
                    .forEach(config -> properties.put(config.getKey().toLowerCase().replaceFirst(prefix, ""), config.getValue()));
        }

        if (options.has("help"))
            usage("Help for Maxwell:");

        setup(options, properties);

        List<?> arguments = options.nonOptionArguments();
        if(!arguments.isEmpty()) {
            usage("Unknown argument(s): " + arguments);
        }
    }
    private void setup(OptionSet options, Properties properties) {
        this.log_level = fetchOption("log_level", options, properties, null);

        this.maxwellMysql       = parseMysqlConfig("", options, properties);
        this.replicationMysql   = parseMysqlConfig("replication_", options, properties);
        this.schemaMysql        = parseMysqlConfig("schema_", options, properties);
        this.gtidMode           = fetchBooleanOption("gtid_mode", options, properties, System.getenv(GTID_MODE_ENV) != null);

        this.databaseName       = fetchOption("schema_database", options, properties, "maxwell");
        this.maxwellMysql.database = this.databaseName;

        this.producerFactory    = fetchProducerFactory(options, properties);
        this.producerType       = fetchOption("producer", options, properties, "stdout");
        this.producerAckTimeout = fetchLongOption("producer_ack_timeout", options, properties, 0L);
        this.bootstrapperType   = fetchOption("bootstrapper", options, properties, "async");
        this.clientID           = fetchOption("client_id", options, properties, "maxwell");
        this.replicaServerID    = fetchLongOption("replica_server_id", options, properties, 6379L);
        this.javascriptFile         = fetchOption("javascript", options, properties, null);

        this.kafkaTopic         	= fetchOption("kafka_topic", options, properties, "maxwell");
        this.deadLetterTopic        = fetchOption("dead_letter_topic", options, properties, null);
        this.kafkaKeyFormat     	= fetchOption("kafka_key_format", options, properties, "hash");
        this.kafkaPartitionKey  	= fetchOption("kafka_partition_by", options, properties, null);
        this.kafkaPartitionColumns  = fetchOption("kafka_partition_columns", options, properties, null);
        this.kafkaPartitionFallback = fetchOption("kafka_partition_by_fallback", options, properties, null);

        this.kafkaPartitionHash 	= fetchOption("kafka_partition_hash", options, properties, "default");
        this.ddlKafkaTopic 		    = fetchOption("ddl_kafka_topic", options, properties, this.kafkaTopic);

        this.pubsubProjectId = fetchOption("pubsub_project_id", options, properties, null);
        this.pubsubTopic 		 = fetchOption("pubsub_topic", options, properties, "maxwell");
        this.ddlPubsubTopic  = fetchOption("ddl_pubsub_topic", options, properties, this.pubsubTopic);

        this.rabbitmqHost           		= fetchOption("rabbitmq_host", options, properties, "localhost");
        this.rabbitmqPort 			= Integer.parseInt(fetchOption("rabbitmq_port", options, properties, "5672"));
        this.rabbitmqUser 			= fetchOption("rabbitmq_user", options, properties, "guest");
        this.rabbitmqPass			= fetchOption("rabbitmq_pass", options, properties, "guest");
        this.rabbitmqVirtualHost    		= fetchOption("rabbitmq_virtual_host", options, properties, "/");
        this.rabbitmqExchange       		= fetchOption("rabbitmq_exchange", options, properties, "maxwell");
        this.rabbitmqExchangeType   		= fetchOption("rabbitmq_exchange_type", options, properties, "fanout");
        this.rabbitMqExchangeDurable 		= fetchBooleanOption("rabbitmq_exchange_durable", options, properties, false);
        this.rabbitMqExchangeAutoDelete 	= fetchBooleanOption("rabbitmq_exchange_autodelete", options, properties, false);
        this.rabbitmqRoutingKeyTemplate   	= fetchOption("rabbitmq_routing_key_template", options, properties, "%db%.%table%");
        this.rabbitmqMessagePersistent    	= fetchBooleanOption("rabbitmq_message_persistent", options, properties, false);
        this.rabbitmqDeclareExchange		= fetchBooleanOption("rabbitmq_declare_exchange", options, properties, true);

        this.redisHost			= fetchOption("redis_host", options, properties, "localhost");
        this.redisPort			= Integer.parseInt(fetchOption("redis_port", options, properties, "6379"));
        this.redisAuth			= fetchOption("redis_auth", options, properties, null);
        this.redisDatabase		= Integer.parseInt(fetchOption("redis_database", options, properties, "0"));
        this.redisPubChannel	= fetchOption("redis_pub_channel", options, properties, "maxwell");
        this.redisListKey		= fetchOption("redis_list_key", options, properties, "maxwell");
        this.redisType			= fetchOption("redis_type", options, properties, "pubsub");

        String kafkaBootstrapServers = fetchOption("kafka.bootstrap.servers", options, properties, null);
        if ( kafkaBootstrapServers != null )
            this.kafkaProperties.setProperty("bootstrap.servers", kafkaBootstrapServers);

        if ( properties != null ) {
            for (Enumeration<Object> e = properties.keys(); e.hasMoreElements(); ) {
                String k = (String) e.nextElement();
                if (k.startsWith("custom_producer.")) {
                    this.customProducerProperties.setProperty(k.replace("custom_producer.", ""), properties.getProperty(k));
                } else if (k.startsWith("kafka.")) {
                    if (k.equals("kafka.bootstrap.servers") && kafkaBootstrapServers != null)
                        continue; // don't override command line bootstrap servers with config files'

                    this.kafkaProperties.setProperty(k.replace("kafka.", ""), properties.getProperty(k));
                }
            }
        }

        this.producerPartitionKey = fetchOption("producer_partition_by", options, properties, "database");
        this.producerPartitionColumns = fetchOption("producer_partition_columns", options, properties, null);
        this.producerPartitionFallback = fetchOption("producer_partition_by_fallback", options, properties, null);

        this.kinesisStream  = fetchOption("kinesis_stream", options, properties, null);
        this.kinesisMd5Keys = fetchBooleanOption("kinesis_md5_keys", options, properties, false);

        this.sqsQueueUri = fetchOption("sqs_queue_uri", options, properties, null);

        this.outputFile = fetchOption("output_file", options, properties, null);

        this.metricsPrefix = fetchOption("metrics_prefix", options, properties, "MaxwellMetrics");
        this.metricsReportingType = fetchOption("metrics_type", options, properties, null);
        this.metricsSlf4jInterval = fetchLongOption("metrics_slf4j_interval", options, properties, 60L);
        // TODO remove metrics_http_port support once hitting v1.11.x
        int port = Integer.parseInt(fetchOption("metrics_http_port", options, properties, "8080"));
        if (port != 8080) {
            LOGGER.warn("metrics_http_port is deprecated, please use http_port");
            this.httpPort = port;
        } else {
            this.httpPort = Integer.parseInt(fetchOption("http_port", options, properties, "8080"));
        }
        this.httpBindAddress = fetchOption("http_bind_address", options, properties, null);
        this.httpPathPrefix = fetchOption("http_path_prefix", options, properties, "/");

        if (!this.httpPathPrefix.startsWith("/")) {
            this.httpPathPrefix = "/" + this.httpPathPrefix;
        }
        this.metricsDatadogType = fetchOption("metrics_datadog_type", options, properties, "udp");
        this.metricsDatadogTags = fetchOption("metrics_datadog_tags", options, properties, "");
        this.metricsDatadogAPIKey = fetchOption("metrics_datadog_apikey", options, properties, "");
        this.metricsDatadogHost = fetchOption("metrics_datadog_host", options, properties, "localhost");
        this.metricsDatadogPort = Integer.parseInt(fetchOption("metrics_datadog_port", options, properties, "8125"));
        this.metricsDatadogInterval = fetchLongOption("metrics_datadog_interval", options, properties, 60L);

        this.metricsJvm = fetchBooleanOption("metrics_jvm", options, properties, false);

        this.diagnosticConfig = new MaxwellDiagnosticContext.Config();
        this.diagnosticConfig.enable = fetchBooleanOption("http_diagnostic", options, properties, false);
        this.diagnosticConfig.timeout = fetchLongOption("http_diagnostic_timeout", options, properties, 10000L);

        this.includeDatabases    = fetchOption("include_dbs", options, properties, null);
        this.excludeDatabases    = fetchOption("exclude_dbs", options, properties, null);
        this.includeTables       = fetchOption("include_tables", options, properties, null);
        this.excludeTables       = fetchOption("exclude_tables", options, properties, null);
        this.blacklistDatabases  = fetchOption("blacklist_dbs", options, properties, null);
        this.blacklistTables     = fetchOption("blacklist_tables", options, properties, null);
        this.filterList          = fetchOption("filter", options, properties, null);
        this.includeColumnValues = fetchOption("include_column_values", options, properties, null);

        if ( options != null && options.has("init_position")) {
            String initPosition = (String) options.valueOf("init_position");
            String[] initPositionSplit = initPosition.split(":");

            if (initPositionSplit.length < 2)
                usageForOptions("Invalid init_position: " + initPosition, "--init_position");

            Long pos = 0L;
            try {
                pos = Long.valueOf(initPositionSplit[1]);
            } catch (NumberFormatException e) {
                usageForOptions("Invalid init_position: " + initPosition, "--init_position");
            }

            Long lastHeartbeat = 0L;
            if ( initPositionSplit.length > 2 ) {
                try {
                    lastHeartbeat = Long.valueOf(initPositionSplit[2]);
                } catch (NumberFormatException e) {
                    usageForOptions("Invalid init_position: " + initPosition, "--init_position");
                }
            }

            this.initPosition = new Position(new BinlogPosition(pos, initPositionSplit[0]), lastHeartbeat);
        }

        this.replayMode =     fetchBooleanOption("replay", options, null, false);
        this.masterRecovery = fetchBooleanOption("master_recovery", options, properties, false);
        this.ignoreProducerError = fetchBooleanOption("ignore_producer_error", options, properties, true);
        this.recaptureSchema = fetchBooleanOption("recapture_schema", options, null, false);

        outputConfig.includesBinlogPosition = fetchBooleanOption("output_binlog_position", options, properties, false);
        outputConfig.includesGtidPosition = fetchBooleanOption("output_gtid_position", options, properties, false);
        outputConfig.includesCommitInfo = fetchBooleanOption("output_commit_info", options, properties, true);
        outputConfig.includesXOffset = fetchBooleanOption("output_xoffset", options, properties, true);
        outputConfig.includesNulls = fetchBooleanOption("output_nulls", options, properties, true);
        outputConfig.includesServerId = fetchBooleanOption("output_server_id", options, properties, false);
        outputConfig.includesThreadId = fetchBooleanOption("output_thread_id", options, properties, false);
        outputConfig.includesSchemaId = fetchBooleanOption("output_schema_id", options, properties, false);
        outputConfig.includesRowQuery = fetchBooleanOption("output_row_query", options, properties, false);
        outputConfig.includesPrimaryKeys = fetchBooleanOption("output_primary_keys", options, properties, false);
        outputConfig.includesPrimaryKeyColumns = fetchBooleanOption("output_primary_key_columns", options, properties, false);
        outputConfig.outputDDL	= fetchBooleanOption("output_ddl", options, properties, false);
        outputConfig.zeroDatesAsNull = fetchBooleanOption("output_null_zerodates", options, properties, false);
        this.excludeColumns     = fetchOption("exclude_columns", options, properties, null);

        String encryptionMode = fetchOption("encrypt", options, properties, "none");
        switch (encryptionMode) {
            case "none":
                outputConfig.encryptionMode = EncryptionMode.ENCRYPT_NONE;
                break;
            case "data":
                outputConfig.encryptionMode = EncryptionMode.ENCRYPT_DATA;
                break;
            case "all":
                outputConfig.encryptionMode = EncryptionMode.ENCRYPT_ALL;
                break;
            default:
                usage("Unknown encryption mode: " + encryptionMode);
                break;
        }

        if (outputConfig.encryptionEnabled()) {
            outputConfig.secretKey = fetchOption("secret_key", options, properties, null);
        }

    }

    private Properties parseFile(String filename, Boolean abortOnMissing) {
        Properties p = readPropertiesFile(filename, abortOnMissing);

        if ( p == null )
            p = new Properties();

        return p;
    }

    private void validatePartitionBy() {
        if ( this.producerPartitionKey == null && this.kafkaPartitionKey != null ) {
            LOGGER.warn("kafka_partition_by is deprecated, please use producer_partition_by");
            this.producerPartitionKey = this.kafkaPartitionKey;
        }

        if ( this.producerPartitionColumns == null && this.kafkaPartitionColumns != null) {
            LOGGER.warn("kafka_partition_columns is deprecated, please use producer_partition_columns");
            this.producerPartitionColumns = this.kafkaPartitionColumns;
        }

        if ( this.producerPartitionFallback == null && this.kafkaPartitionFallback != null ) {
            LOGGER.warn("kafka_partition_by_fallback is deprecated, please use producer_partition_by_fallback");
            this.producerPartitionFallback = this.kafkaPartitionFallback;
        }

        String[] validPartitionBy = {"database", "table", "primary_key", "transaction_id", "column"};
        if ( this.producerPartitionKey == null ) {
            this.producerPartitionKey = "database";
        } else if ( !ArrayUtils.contains(validPartitionBy, this.producerPartitionKey) ) {
            usageForOptions("please specify --producer_partition_by=database|table|primary_key|transaction_id|column", "producer_partition_by");
        } else if ( this.producerPartitionKey.equals("column") && StringUtils.isEmpty(this.producerPartitionColumns) ) {
            usageForOptions("please specify --producer_partition_columns=column1 when using producer_partition_by=column", "producer_partition_columns");
        } else if ( this.producerPartitionKey.equals("column") && StringUtils.isEmpty(this.producerPartitionFallback) ) {
            usageForOptions("please specify --producer_partition_by_fallback=[database, table, primary_key, transaction_id] when using producer_partition_by=column", "producer_partition_by_fallback");
        }

    }

    private void validateFilter() {
        if ( this.filter != null )
            return;
        try {
            if ( this.filterList != null ) {
                this.filter = new Filter(this.databaseName, filterList);
            } else {
                boolean hasOldStyleFilters =
                        includeDatabases != null ||
                                excludeDatabases != null ||
                                includeTables != null ||
                                excludeTables != null ||
                                blacklistDatabases != null ||
                                blacklistTables != null ||
                                includeColumnValues != null;

                if ( hasOldStyleFilters ) {
                    this.filter = Filter.fromOldFormat(
                            this.databaseName,
                            includeDatabases,
                            excludeDatabases,
                            includeTables,
                            excludeTables,
                            blacklistDatabases,
                            blacklistTables,
                            includeColumnValues
                    );
                } else {
                    this.filter = new Filter(this.databaseName, "");
                }
            }
        } catch (InvalidFilterException e) {
            usageForOptions("Invalid filter options: " + e.getLocalizedMessage(), "filter");
        }
    }


    @Override
    public void validate(){
        validatePartitionBy();
        validateFilter();

        if ( this.producerType.equals("kafka") ) {
            if ( !this.kafkaProperties.containsKey("bootstrap.servers") ) {
                usageForOptions("You must specify kafka.bootstrap.servers for the kafka producer!", "kafka");
            }

            if ( this.kafkaPartitionHash == null ) {
                this.kafkaPartitionHash = "default";
            } else if ( !this.kafkaPartitionHash.equals("default")
                    && !this.kafkaPartitionHash.equals("murmur3") ) {
                usageForOptions("please specify --kafka_partition_hash=default|murmur3", "kafka_partition_hash");
            }

            if ( !this.kafkaKeyFormat.equals("hash") && !this.kafkaKeyFormat.equals("array") )
                usageForOptions("invalid kafka_key_format: " + this.kafkaKeyFormat, "kafka_key_format");

        } else if ( this.producerType.equals("file")
                && this.outputFile == null) {
            usageForOptions("please specify --output_file=FILE to use the file producer", "--producer", "--output_file");
        } else if ( this.producerType.equals("kinesis") && this.kinesisStream == null) {
            usageForOptions("please specify a stream name for kinesis", "kinesis_stream");
        } else if (this.producerType.equals("sqs") && this.sqsQueueUri == null) {
            usageForOptions("please specify a queue uri for sqs", "sqs_queue_uri");
        }

        if ( !this.bootstrapperType.equals("async")
                && !this.bootstrapperType.equals("sync")
                && !this.bootstrapperType.equals("none") ) {
            usageForOptions("please specify --bootstrapper=async|sync|none", "--bootstrapper");
        }

        if (this.maxwellMysql.sslMode == null) {
            this.maxwellMysql.sslMode = SSLMode.DISABLED;
        }

        if ( this.maxwellMysql.host == null ) {
            LOGGER.warn("maxwell mysql host not specified, defaulting to localhost");
            this.maxwellMysql.host = "localhost";
        }

        if ( this.replicationMysql.host == null
                || this.replicationMysql.user == null ) {

            if (this.replicationMysql.host != null
                    || this.replicationMysql.user != null
                    || this.replicationMysql.password != null) {
                usageForOptions("Please specify all of: replication_host, replication_user, replication_password", "--replication");
            }

            this.replicationMysql = new MaxwellMysqlConfig(
                    this.maxwellMysql.host,
                    this.maxwellMysql.port,
                    null,
                    this.maxwellMysql.user,
                    this.maxwellMysql.password,
                    this.maxwellMysql.sslMode
            );

            this.replicationMysql.jdbcOptions = this.maxwellMysql.jdbcOptions;
        }

        if (this.replicationMysql.sslMode == null) {
            this.replicationMysql.sslMode = this.maxwellMysql.sslMode;
        }

        if (gtidMode && masterRecovery) {
            usageForOptions("There is no need to perform master_recovery under gtid_mode", "--gtid_mode");
        }

        if (outputConfig.includesGtidPosition && !gtidMode) {
            usageForOptions("output_gtid_position is only support with gtid mode.", "--output_gtid_position");
        }

        if (this.schemaMysql.host != null) {
            if (this.schemaMysql.user == null || this.schemaMysql.password == null) {
                usageForOptions("Please specify all of: schema_host, schema_user, schema_password", "--schema");
            }

            if (this.replicationMysql.host == null) {
                usageForOptions("Specifying schema_host only makes sense along with replication_host");
            }
        }

        if (this.schemaMysql.sslMode == null) {
            this.schemaMysql.sslMode = this.maxwellMysql.sslMode;
        }


        if ( this.metricsDatadogType.contains("http") && StringUtils.isEmpty(this.metricsDatadogAPIKey) ) {
            usageForOptions("please specify metrics_datadog_apikey when metrics_datadog_type = http");
        }

        if ( this.excludeColumns != null ) {
            for ( String s : this.excludeColumns.split(",") ) {
                try {
                    outputConfig.excludeColumns.add(compileStringToPattern(s));
                } catch ( InvalidFilterException e ) {
                    usage("invalid exclude_columns: '" + this.excludeColumns + "': " + e.getMessage());
                }
            }
        }

        if (outputConfig.encryptionEnabled() && outputConfig.secretKey == null)
            usage("--secret_key required");

        if ( this.javascriptFile != null ) {
            try {
                this.scripting = new Scripting(this.javascriptFile);
            } catch ( Exception e ) {
                LOGGER.error("Error setting up javascript: ", e);
               System.exit(1);
            }
        }
    }
}
