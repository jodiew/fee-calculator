(ns fee-calculator.services.test-helpers
  (:require [clojure.string :refer [split-lines split]]))

(defn read-csv-test-cases [data]
  (let [[head & lines] (split-lines data)
        split-head (split head #",")
        split-body (map #(split % #",") lines)
        body (map (fn [line]
                    (map #(.parseFloat js/Number %) line))
                  split-body)]
    (map #(zipmap split-head %) body)))

(defn test-allocations [test-case]
  [{:type :cash-account
    :amount (get test-case "cash_account")}
   {:type :company-fund
    :amount (get test-case "company_funds")}
   {:type :managed-fund-other
    :amount (get test-case "other_funds")}
   {:type :csx-security
    :amount (get test-case "other_assets")}
   {:type :managed-fund-fx
    :amount 0.0}
   {:type :managed-account
    :amount 0.0}
   {:type :term-deposit
    :amount 0.0}])

(defn test-expected-fees [test-case]
  {:admin (.toFixed (get test-case "admin_fee") 2)
   :promoter (.toFixed (get test-case "promoter_fee") 2)
   :account (.toFixed (get test-case "account_fee") 2)
   :orfr (.toFixed (+ (get test-case "expense_recovery_fee")
                      (get test-case "orfr_fee"))
                   2)
   :total (.toFixed (get test-case "total_fee") 2)})
