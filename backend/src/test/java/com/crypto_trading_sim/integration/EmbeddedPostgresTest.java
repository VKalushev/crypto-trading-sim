package com.crypto_trading_sim.integration;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureEmbeddedDatabase(
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY,
        refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD
)
public @interface EmbeddedPostgresTest {
}