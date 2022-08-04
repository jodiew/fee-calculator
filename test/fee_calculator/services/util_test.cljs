(ns fee-calculator.services.util-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [decimal.core :as d]
            [fee-calculator.services.util :refer [to-fixed-2]]))

(deftest to-fixed-2-test
  (testing "simple map"
    (is (= {:round-down "0.33"
            :round-up "0.67"}
           (to-fixed-2 {:round-down (d// (d/decimal 1) (d/decimal 3))
                        :round-up (d// (d/decimal 2) (d/decimal 3))}))))
  
  (testing "fee map"
    (is (= {:admin "3.75"
            :promoter "2.00"
            :account "150.00"
            :orfr "80.00"
            :total "235.75"}
           (to-fixed-2 {:admin (d/decimal 3.75)
                        :promoter (d/decimal 2)
                        :account (d/decimal 150)
                        :orfr (d/decimal 80)
                        :total (d/decimal 235.75)})))))