(ns fee-calculator.db
  (:require [fee-calculator.data.securities :refer [securities]]))

;; -- Default app-db Value ----------------------------------------

(def default-db
  {:product :product-a
   :investment-amount 100000
   :allocations {:cash-account  {:name "Cash Account"
                                 :type :cash-account
                                 :amount 2500
                                 :tooltip-text "Your Cash Account is your working cash account which is used for settling all purchases, receiving dividends and paying fees for transactions.  Your Cash Account is an interest generating account which requires a minimum balance of $2,500."}
                 :term-deposits {:name "Term Deposits"
                                 :type :term-deposits
                                 :amount 0
                                 :tooltip-text "You can access a wide range of term deposits with varying fixed terms and interest rates from a range of well-known providers."}
                 :csx-security {:name "CSX Listed Securities"
                                :type :csx-security
                                :amount 0
                                :tooltip-text "You can access CSX Listed Securities as well as Exchange Traded Funds (ETFs) and Listed Investment Companies (LICs)."}
                 :managed-account {:name "Managed Account Model Portfolios"
                                   :type :managed-account
                                   :amount 0
                                   :tooltip-text "The underlying assets which form part of each Managed Account Model Portfolio determines your administration fees."}
                 "XXXXXX001" {:name "Company Fund 1"
                              :type :company-fund
                              :amount 0}
                 "XXXXXX002" {:name "Company Fund 2"
                              :type :company-fund
                              :amount 0}
                 "XXXXXX003" {:name "Company Fund 3"
                              :type :company-fund
                              :amount 0}
                 "XXXXXX004" {:name "Company Fund 4"
                              :type :company-fund
                              :amount 0}
                 }
   :company-ids ["XXXXXX001"
                 "XXXXXX002"
                 "XXXXXX003"
                 "XXXXXX004"]
   :managed-funds-ids []
   :active-step 0
   :securities (-> securities
                   (assoc-in ["XXXXXX001" :in-use?] true)
                   (assoc-in ["XXXXXX002" :in-use?] true)
                   (assoc-in ["XXXXXX003" :in-use?] true)
                   (assoc-in ["XXXXXX004" :in-use?] true))
   :search-string ""
   :admin-fee-option []
   :asset-class-option []
   :open-company-funds true
   :open-managed-funds true
   :open-securities false})
