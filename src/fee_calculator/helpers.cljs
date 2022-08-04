(ns fee-calculator.helpers
  (:import (goog.i18n NumberFormat)
           (goog.i18n.NumberFormat Format)))

(def decimal-format (NumberFormat. Format/DECIMAL))

(defn number->decimal [num]
  (.format decimal-format (str num)))

(defn decimal->number [string]
  (.parse decimal-format string))

(def percent-format (.setMinimumFractionDigits (NumberFormat. Format/DECIMAL) 2))

(defn number->percent [num]
  (.format percent-format (str num)))

(defn number->percent-sign [num]
  (str (.format percent-format (str num)) "%"))

(defn percent->number [string]
  (let [number (.parse percent-format string)]
    (if (NaN? number)
      0
      number)))

(def currency-format (.setMaximumFractionDigits (NumberFormat. Format/DECIMAL) 0))

(defn number->currency [num]
  (str "$" (.format currency-format (str num))))

(defn add-string-numbers [a b]
  (+ (.parseFloat js/Number a) (.parseFloat js/Number b)))
