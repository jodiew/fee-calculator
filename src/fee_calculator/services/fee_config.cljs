(ns fee-calculator.services.fee-config)

(defn fee-config [type calculator include exclude]
  {:type type
   :calculator calculator
   :include include
   :exclude exclude})

(defn filter-allocations [{:keys [include exclude]} allocations]
  (if (seq include)
    (filter #(some #{(:type %)} include) allocations)
    (if (seq exclude)
      (filter #(not (some #{(:type %)} exclude)) allocations)
      allocations)))

(defn allocations-total [fee-config allocations]
  (reduce (fn [acc allocation]
            (+ acc (:amount allocation)))
          0
          (filter-allocations fee-config allocations)))

(defn calculate [fee-config allocations]
  ((:calculator fee-config) (allocations-total fee-config allocations)))