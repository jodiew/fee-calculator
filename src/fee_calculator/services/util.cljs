(ns fee-calculator.services.util)

(defn to-fixed-2 [fees]
  (reduce-kv (fn [fees fee-type fee]
               (assoc fees fee-type (.toFixed fee 2)))
             {}
             fees))