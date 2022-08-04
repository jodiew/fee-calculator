(ns fee-calculator.services.fees-calculator
  (:require [decimal.core :as d]
            
            [fee-calculator.services.fee-config :refer [calculate allocations-total filter-allocations]]
            [fee-calculator.services.util :refer [to-fixed-2]]))

(defn breakdowns [fee-configs allocations fees]
  (let [zero-fees (reduce (fn [acc fee]
                            (assoc acc (:type fee) 0.0))
                          {}
                          fee-configs)
        breakdown-zeros (reduce (fn [acc allocation]
                                  (assoc acc (:type allocation) {:assets (:amount allocation)
                                                                 :fees zero-fees}))
                                {}
                                allocations)
        breakdown (reduce (fn [breakdown fee]
                            (if (= (:type fee) :orfr)
                              breakdown
                              (let [assets (allocations-total fee allocations)]
                                (if (> assets 0)
                                  (reduce (fn [breakdown allocation]
                                            (let [pro-rata-fee (-> (d/decimal (:amount allocation))
                                                                   (d// assets)
                                                                   (d/* (get fees (:type fee))))]
                                              (update-in breakdown [(:type allocation) :fees (:type fee)] #(d/+ (d/decimal %) pro-rata-fee))))
                                          breakdown
                                          (filter-allocations fee allocations))
                                  breakdown))))
                          breakdown-zeros
                          fee-configs)]
    (reduce-kv (fn [breakdown k a]
                 (-> breakdown
                     (assoc-in [k :fees :total] (reduce d/+ (d/decimal 0) (vals (:fees a))))
                     (update-in [k :fees] #(to-fixed-2 %))))
               breakdown
               breakdown)))

(defn fees-calculator [fee-configs]
  (fn [allocations]
    (let [fees (reduce (fn [fees fee]
                         (assoc fees (:type fee) (calculate fee allocations)))
                       {}
                       fee-configs)
          fees-total (assoc fees :total (reduce d/+ (d/decimal 0) (vals fees)))
          breakdown (breakdowns fee-configs allocations fees-total)]
      (assoc (to-fixed-2 fees-total) :breakdown breakdown))))
