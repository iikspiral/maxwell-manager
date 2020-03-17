package com.puyoma.maxwell.zendesk;

import com.zendesk.maxwell.MaxwellConfig;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.MaxwellMysqlStatus;
import com.zendesk.maxwell.bootstrap.BootstrapController;
import com.zendesk.maxwell.producer.AbstractProducer;
import com.zendesk.maxwell.replication.BinlogConnectorReplicator;
import com.zendesk.maxwell.replication.Position;
import com.zendesk.maxwell.schema.MysqlSchemaStore;
import com.zendesk.maxwell.schema.SchemaStoreSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: create by dawei
 * @version: v1.0
 * @description: com.puyoma.maxwell.zendesk
 * @date:2020/3/11
 */
public class Maxwell extends com.zendesk.maxwell.Maxwell {
    static final Logger LOGGER = LoggerFactory.getLogger(Maxwell.class);

    public Maxwell(MaxwellConfig config) throws SQLException, URISyntaxException {
        super(config);
    }

    protected Maxwell(MaxwellContext context) throws SQLException, URISyntaxException {
        super(context);
    }

    public void start() throws Exception {
        try {
            startInner();
        } catch ( Exception e) {
            this.context.terminate(e);
        } finally {
            onReplicatorEnd();
            this.terminate();
        }

        Exception error = this.context.getError();
        if (error != null) {
            throw error;
        }
    }
    private void startInner() throws Exception {
        try (Connection connection = this.context.getReplicationConnection();
             Connection rawConnection = this.context.getRawMaxwellConnection() ) {
            MaxwellMysqlStatus.ensureReplicationMysqlState(connection);
            MaxwellMysqlStatus.ensureMaxwellMysqlState(rawConnection);
            if (config.gtidMode) {
                MaxwellMysqlStatus.ensureGtidMysqlState(connection);
            }

            SchemaStoreSchema.ensureMaxwellSchema(rawConnection, this.config.databaseName);

            try ( Connection schemaConnection = this.context.getMaxwellConnection() ) {
                SchemaStoreSchema.upgradeSchemaStoreSchema(schemaConnection);
            }
        }

        AbstractProducer producer = this.context.getProducer();

        Position initPosition = getInitialPosition();
        logBanner(producer, initPosition);
        this.context.setPosition(initPosition);

        MysqlSchemaStore mysqlSchemaStore = new MysqlSchemaStore(this.context, initPosition);
        BootstrapController bootstrapController = this.context.getBootstrapController(mysqlSchemaStore.getSchemaID());

        if (config.recaptureSchema) {
            mysqlSchemaStore.captureAndSaveSchema();
        }

        mysqlSchemaStore.getSchema(); // trigger schema to load / capture before we start the replicator.

        this.replicator = new BinlogConnectorReplicator(
                mysqlSchemaStore,
                producer,
                bootstrapController,
                config.replicationMysql,
                config.replicaServerID,
                config.databaseName,
                context.getMetrics(),
                initPosition,
                false,
                config.clientID,
                context.getHeartbeatNotifier(),
                config.scripting,
                context.getFilter(),
                config.outputConfig
        );

        context.setReplicator(replicator);
        this.context.start();
        this.onReplicatorStart();

        replicator.runLoop();
    }
    static String bootString = "Maxwell v%s is booting (%s), starting at %s";
    private void logBanner(AbstractProducer producer, Position initialPosition) {
        String producerName = producer.getClass().getSimpleName();
        LOGGER.info(String.format(bootString, getMaxwellVersion(), producerName, initialPosition.toString()));
    }

    public void stop(){
        this.context.terminate(null);
    }
}
