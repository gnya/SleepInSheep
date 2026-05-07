package io.github.gnya.sheep_mod.core;

import io.github.gnya.sheep_mod.core.sensing.BedSheepSensor;
import java.util.function.Supplier;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class SheepModSensorTypes {
  public static final SensorType<BedSheepSensor> NEAREST_BED_SHEEP =
      register("bed_sheep", BedSheepSensor::new);

  public static <S extends Sensor<?>> SensorType<S> register(
      final String name, final Supplier<S> factory) {
    var type = new SensorType<>(factory);

    SheepModRegistries.SENSOR_TYPES.register(name, () -> type);

    return type;
  }

  public static void init() {}
}
