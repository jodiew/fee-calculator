(ns fee-calculator.services.fees-calculator-test
  (:require [cljs.test :refer-macros [deftest testing is]]

            [fee-calculator.services.test-helpers :refer [read-csv-test-cases
                                                          test-allocations
                                                          test-expected-fees]]
            [fee-calculator.services.a-fees-calculator :refer [a-fees-calculator]]
            [fee-calculator.services.b-fees-calculator :refer [b-fees-calculator]]
            [fee-calculator.services.a-fees-calculator-test-data :refer [a-test-data]]
            [fee-calculator.services.b-fees-calculator-test-data :refer [b-test-data]]))

(deftest super-fees-calculator-test
  (doseq [test-case (read-csv-test-cases a-test-data)]
    (let [case-num (get test-case "case")
          allocations (test-allocations test-case)
          fees (select-keys (a-fees-calculator allocations)
                            [:admin :promoter :account :orfr :total])
          expected-fees (test-expected-fees test-case)]
      (testing (str "case" case-num)
        (is (= fees expected-fees))))))

(deftest idps-fees-calculator-test
  (doseq [test-case (read-csv-test-cases b-test-data)]
    (let [case-num (get test-case "case")
          allocations (test-allocations test-case)
          fees (select-keys (b-fees-calculator allocations)
                            [:admin :promoter :account :orfr :total])
          expected-fees (test-expected-fees test-case)]
      (testing (str "case" case-num)
        (is (= fees expected-fees))))))
