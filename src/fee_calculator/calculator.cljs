(ns fee-calculator.calculator)

(defn investment-percent [amount total]
  (if (zero? amount)
    0
    (/ (* amount 100) total)))