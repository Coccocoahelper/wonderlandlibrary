/* November.lol © 2023 */
package lol.november.feature.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
  String[] aliases();

  String description() default "No description was provided for this command";

  String syntax() default "";
}
