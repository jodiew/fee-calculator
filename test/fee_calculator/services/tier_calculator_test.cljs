(ns fee-calculator.services.tier-calculator-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [fee-calculator.services.tier-calculator :refer [tier tier-calculator]]))

(deftest tier-calculator-test
  (testing "flat rate"
    (let [flat-calc (tier-calculator [(tier 0 100 0)])]
      (is (= (flat-calc 0) 0))
      (is (= (flat-calc 1) 100))
      (is (= (flat-calc 1000000) 100))))
  
  (testing "tiered rate"
    (let [tier-calc (tier-calculator [(tier 0       0 0.0015)
                                      (tier 250000  0 0.001)
                                      (tier 500000  0 0.0005)
                                      (tier 1500000 0 0)])]
      (is (= (tier-calc 0) 0))
      (is (= (tier-calc 250000) (* 250000 0.0015)))
      (is (= (tier-calc 500000) (+ (* 250000 0.0015) (* (- 500000 250000) 0.001))))
      (is (= (tier-calc 1500000) (+ (* 250000 0.0015) (* (- 500000 250000) 0.001) (* (- 1500000 500000) 0.0005))))
      (is (= (tier-calc 2000000) (+ (* 250000 0.0015) (* (- 500000 250000) 0.001) (* (- 1500000 500000) 0.0005))))))
  
  (testing "tiered and flat rate"
    (let [tf-calc (tier-calculator [(tier 0       50 0.0003)
                                    (tier 1000000 0  0)])]
      (is (= (tf-calc 0) 0))
      (is (= (tf-calc 1000000) (+ 50 (* 1000000 0.0003))))
      (is (= (tf-calc 2000000) (+ 50 (* 1000000 0.0003)))))))
