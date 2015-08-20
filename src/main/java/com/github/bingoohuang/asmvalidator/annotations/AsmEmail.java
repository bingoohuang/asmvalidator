package com.github.bingoohuang.asmvalidator.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/*
http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
Email Regular Expression Pattern

^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*
      @[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$;
Description

^			#start of the line
  [_A-Za-z0-9-\\+]+	#  must start with string in the bracket [ ], must contains one or more (+)
  (			#   start of group #1
    \\.[_A-Za-z0-9-]+	#     follow by a dot "." and string in the bracket [ ], must contains one or more (+)
  )*			#   end of group #1, this group is optional (*)
    @			#     must contains a "@" symbol
     [A-Za-z0-9-]+      #       follow by string in the bracket [ ], must contains one or more (+)
      (			#         start of group #2 - first level TLD checking
       \\.[A-Za-z0-9]+  #           follow by a dot "." and string in the bracket [ ], must contains one or more (+)
      )*		#         end of group #2, this group is optional (*)
      (			#         start of group #3 - second level TLD checking
       \\.[A-Za-z]{2,}  #           follow by a dot "." and string in the bracket [ ], with minimum length of 2
      )			#         end of group #3
$			#end of the line
 */
@Documented
@AsmRegex("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
@AsmConstraint(message = "邮箱格式不正确")
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmEmail {

}
