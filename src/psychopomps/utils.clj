(ns psychopomps.utils
  "A namespace for utility functions")

(defmacro while-let
  "Repeatedly executes body while test expression is true, evaluating
  the body with binding-form bound to the value of test."
  [[form test] & body]
  `(loop [~form ~test]
     (when ~form
       ~@body
       (recur ~test))))
