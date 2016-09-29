package io.github.achmadns;

import com.addthis.metrics3.reporter.config.ReporterConfig;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import javaslang.collection.Stream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class MetricsTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void show_metrics() throws InterruptedException, IOException {
        final MetricRegistry metrics = new MetricRegistry();
        metrics.register("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        metrics.register("jvm.gc", new GarbageCollectorMetricSet());
        metrics.register("jvm.memory", new MemoryUsageGaugeSet());
        metrics.register("jvm.fd.usage", new FileDescriptorRatioGauge());
        ReporterConfig.loadFromFile(getClass().getClassLoader().getResource("metrics-sink.yml").getFile()).enableAll(metrics);

        Stream.range(0, 3600).forEach(n -> {
            final Timer.Context timer = metrics.timer("test").time();
            try {
                Thread.sleep(1000);
                log.info("Slept at {}", n);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                timer.stop();

            }
        });


    }


}
