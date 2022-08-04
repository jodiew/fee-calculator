(ns fee-calculator.components.fee-breakdown-graph
  (:require [re-frame.core :refer [subscribe]]

            [reagent-mui.icons.paid :refer [paid]]
            [reagent-mui.icons.circle :refer [circle]]

            [fee-calculator.subs :as subs]
            [fee-calculator.styles :as styles]))

(defn fee-breakdown-graph []
  (let [portfolio-high @(subscribe [::subs/portfolio-high])
        portfolio-low @(subscribe [::subs/portfolio-low])
        fee-orfr @(subscribe [::subs/fee-orfr])
        fee-high @(subscribe [::subs/fee-high])
        fee-low @(subscribe [::subs/fee-low])]
    [:div {:class (styles/fee-breakdown-graph)}
     [:div.legend
      [:div "100%"]
      [:div
       [:div "Investment amount:"]
       [:div @(subscribe [::subs/investment-amount-formatted])]]
      [:div]
      [:div
       [:div "Total Administration fees:"]
       [:div (str "$" @(subscribe [::subs/administration-fee-amount]) " p.a.")]]]
     [:div.graph
      [:div.label-col
       [:div "100%"]
       [:div "50%"]
       [:div "0%"]]
      [:div.portfolio-col
       [:div {:class (styles/height portfolio-high)}
        (str (.toFixed portfolio-high 0) "%")]
       [:div {:class (styles/height portfolio-low)}
        (str (.toFixed portfolio-low 0) "%")]]
      [:div.mapping-col
       [:div
        [:svg
         (for [{:keys [id from to]} @(subscribe [::subs/mappings])]
           ^{:key id}
           [:svg
            [:line {:x1 "0%"
                    :x2 "10%"
                    :y1 (str from "%")
                    :y2 (str from "%")}]
            [:line {:x1 "10%"
                    :x2 "90%"
                    :y1 (str from "%")
                    :y2 (str to "%")}]
            [:line {:x1 "90%"
                    :x2 "100%"
                    :y1 (str to "%")
                    :y2 (str to "%")}]])]]]
      [:div.fee-col
       [:div {:class (styles/height fee-orfr)}
        (str (.toFixed fee-orfr 0) "%")]
       [:div {:class (styles/height fee-high)}
        (str (.toFixed fee-high 0) "%")]
       [:div {:class (styles/height fee-low)}
        (str (.toFixed fee-low 0) "%")]]]
     [:div.legend
      [:div "100%"]
      [:div "Your selected portfolio"]
      [:div]
      [:div "Investment choice fee impact on total administration fees"]]
     [:div.icons
      [:div
       [paid {:html-color "red"}]
       [:span
        "Company Funds"]]
      [:div
       [paid {:html-color "black"}]
       [:span
        "Other investment options"]]
      [:div
       [circle {:html-color "lightblue"}]
       [:span
        "Expense Recovery Fee"]]]]))
