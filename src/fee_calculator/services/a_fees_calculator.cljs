(ns fee-calculator.services.a-fees-calculator
  (:require [fee-calculator.services.tier-calculator :refer [tier tier-calculator]]
            [fee-calculator.services.fee-config :refer [fee-config]]
            [fee-calculator.services.fees-calculator :refer [fees-calculator]]))

(def admin-fee-calc (tier-calculator [(tier 0       0 0.0015)
                                      (tier 250000  0 0.001)
                                      (tier 500000  0 0.0005)
                                      (tier 1500000 0 0)]))

(def promoter-fee-calc (tier-calculator [(tier 0       0 0.0008)
                                         (tier 250000  0 0.0006)
                                         (tier 1000000 0 0)]))

(def account-fee-calc (tier-calculator [(tier 0 150 0)]))

(def orfr-fee-calc (tier-calculator [(tier 0       50 0.0003)
                                     (tier 1000000 0  0)]))

(def fee-configuration [(fee-config :admin admin-fee-calc [] [:company-fund])
                        (fee-config :promoter promoter-fee-calc [] [:company-fund])
                        (fee-config :account account-fee-calc [] [])
                        (fee-config :orfr orfr-fee-calc [] [])])

(def a-fees-calculator (fees-calculator fee-configuration))

(a-fees-calculator [{:type :cash-account
                     :amount 2500}
                    {:type :company-fund
                     :amount (- 100000 2500)}])