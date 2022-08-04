(ns fee-calculator.services.tier-calculator
  (:require [decimal.core :as d]))

(defn tier [floor base rate]
  {:floor (d/decimal floor)
   :base (d/decimal base)
   :rate (d/decimal rate)})

(defn tier-calculator [tiers]
  (fn [amount]
    (:fee (reduce (fn [{:keys [amount fee]} {:keys [floor base rate]}]
                    (let [tier-amount (d/- amount floor)]
                      (if (d/> tier-amount 0)
                        (let [tier-fee (d/+ (d/* tier-amount rate) base)]
                          {:amount (d/- amount tier-amount)
                           :fee (d/+ fee tier-fee)})
                        {:amount amount
                         :fee fee})))
                  {:amount (d/decimal amount)
                   :fee (d/decimal 0.0)}
                  (sort-by :floor d/> tiers)))))
