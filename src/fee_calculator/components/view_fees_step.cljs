(ns fee-calculator.components.view-fees-step
  (:require-macros [reagent-mui.util :refer [react-component]])
  (:require [re-frame.core :refer [subscribe]]
            [cljsjs.moment]

            [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.material.box :refer [box]]
            [reagent-mui.material.table :refer [table]]
            [reagent-mui.material.table-container :refer [table-container]]
            [reagent-mui.material.table-body :refer [table-body]]
            [reagent-mui.material.table-row :refer [table-row]]
            [reagent-mui.material.table-cell :refer [table-cell]]
            [reagent-mui.material.paper :refer [paper]]
            [reagent-mui.icons.paid :refer [paid]]

            [fee-calculator.subs :as subs]
            [fee-calculator.helpers :refer [number->currency
                                            number->percent-sign]]
            [fee-calculator.components.fee-slider :refer [fee-slider]]
            [fee-calculator.components.fee-breakdown-graph :refer [fee-breakdown-graph]]))

(defn allocation-row
  ([id] [allocation-row {:variant "standard"} id])
  ([{:keys [variant]} id]
   (let [{:keys [name]} @(subscribe [::subs/allocation id])
         amount @(subscribe [::subs/allocation-amount id])
         percent @(subscribe [::subs/allocation-percent id])]
     (when (> amount 0)
       [table-row {:sx {:td {:border 0}}}
        [table-cell (str name " - " id)]
        [table-cell (number->currency amount)]
        [table-cell (number->percent-sign percent)]
        [table-cell [paid {:html-color (if (= variant "company")
                                         "red"
                                         "black")}]]]))))

(defn styled-table-cell
  ([text]
   [styled-table-cell {} text])
  ([props text]
   [table-cell
    (merge props {:sx {:font-size 18}})
    text]))

(defn view-fees-step []
  (let [product (str "Company " @(subscribe [::subs/product-formatted]))]
    [:<>
     [:section
      [typography {:variant "h5"
                   :gutter-bottom true}
       "Your Administration Fee Breakdown"]
      [typography {:variant "body2"}
       (str "The following information is a breakdown of your administration fees,
          based on the information you entered into our calculator as "
            (.format (js/moment) "DD MMMM YYYY")
            ". These are not additional costs – we have simply unbundled
          all the costs to provide you the detail.")]]
     [fee-slider]
     [:section
      [typography {:variant "h5"
                   :gutter-bottom true}
       "Administration Fee Summary"]
      [table-container {:component (react-component [props]
                                                    [paper props])}
       [table
        [table-body
         [table-row
          [table-cell "Asset based fee:"]
          [table-cell (str "$" @(subscribe [::subs/asset-based-fee-amount]) " p.a.")]
          [table-cell (str @(subscribe [::subs/asset-based-fee-percent]) "% p.a.")]]
         [table-row
          [table-cell "Account keeping fee:"]
          [table-cell (str "$" @(subscribe [::subs/account-keeping-fee-amount]) " p.a.")]
          [table-cell (str @(subscribe [::subs/account-keeping-fee-percent]) "% p.a.")]]
         [table-row
          [table-cell "Expense recovery fee:"]
          [table-cell (str "$" @(subscribe [::subs/expense-recovery-fee-amount]) " p.a.")]
          [table-cell (str @(subscribe [::subs/expense-recovery-fee-percent]) "% p.a.")]]
         [table-row
          [styled-table-cell "Administration fee:"]
          [styled-table-cell (str "$" @(subscribe [::subs/administration-fee-amount]) " p.a.")]
          [styled-table-cell (str @(subscribe [::subs/administration-fee-percent]) "% p.a.")]]]]]]
     [:section
      [typography {:variant "h5"
                   :gutter-bottom true
                   :sx {:mt 3}}
       "Your Portfolio Summary"]
      [table-container {:component (react-component [props]
                                                    [paper props])}
       [table
        [table-body
         (when (> @(subscribe [::subs/company-total-amount]) 0)
           [:<> [table-row
                 [styled-table-cell "Company Funds"]
                 [styled-table-cell (number->currency @(subscribe [::subs/company-total-amount]))]
                 [styled-table-cell (number->percent-sign @(subscribe [::subs/company-total-percent]))]
                 [styled-table-cell [paid {:html-color "red"}]]]
            (for [id @(subscribe [::subs/company-ids])]
              ^{:key id}
              [allocation-row {:variant "company"} id])])
         (for [{:keys [id label]} [{:id :cash-account
                                    :label "Cash Account"}
                                   {:id :term-deposits
                                    :label "Term Deposits"}
                                   {:id :csx-security
                                    :label "CSX Listed Securities"}
                                   {:id :managed-account
                                    :label "Managed Account Model Portfolios"}]]
           (when (> @(subscribe [::subs/allocation-amount id]) 0)
             [table-row
              [styled-table-cell label]
              [styled-table-cell (number->currency @(subscribe [::subs/allocation-amount id]))]
              [styled-table-cell (number->percent-sign @(subscribe [::subs/allocation-percent id]))]
              [styled-table-cell [paid]]]))
         (when (> @(subscribe [::subs/mf-total-amount]) 0)
           [:<>
            [table-row
             [styled-table-cell "Managed Funds"]
             [styled-table-cell (number->currency @(subscribe [::subs/mf-total-amount]))]
             [styled-table-cell (number->percent-sign @(subscribe [::subs/mf-total-percent]))]
             [styled-table-cell [paid]]]
            (for [id @(subscribe [::subs/managed-funds-ids])]
              ^{:key id}
              [allocation-row id])])]]]]
     [:section
      [typography {:variant "h5"
                   :gutter-bottom true
                   :sx {:mt 3}}
       "How Do Your Investment Choices Impact Your Fees"]
      [typography {:variant "body2"}
       "The Company Managed Funds cost the least to administer as we do not
     charge an asset-based administration fee. The following graph shows
     the make-up of your administration fees based on your selection
     between Company Managed Funds and other investments and how this
     has contributed to the proportion of your total fees."]
      [box {:sx {:mt 1
                 :height 0.15}}
       [paper {:sx {:p 3}}
        [fee-breakdown-graph]]]]
     [:section
      [typography {:variant "h5"
                   :gutter-bottom true
                   :sx {:mt 3}}
       "The Detail"]
      [typography {:variant "h6"}
       "Total Administration fee"]
      [typography {:variant "body2"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla placerat a nisl vel eleifend. Vestibulum blandit dictum vestibulum. Praesent vestibulum ac ligula venenatis volutpat. Sed ornare nisl ac libero pellentesque elementum. Ut fringilla ullamcorper ipsum, vitae dictum enim semper vitae. Vestibulum semper ipsum velit, at ullamcorper urna vehicula vel. Praesent eu varius enim, ullamcorper tempor ante. Mauris luctus interdum hendrerit. Vestibulum sit amet scelerisque nunc. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras imperdiet nisl sed enim cursus, vulputate imperdiet neque tincidunt. Vestibulum dolor ligula, egestas vitae interdum id, posuere eget enim."]]
     [:section
      [typography {:variant "h6"}
       "Asset based fee"]
      [typography {:variant "body2"}
       "Vestibulum eleifend odio at ante tempus pulvinar ac eu mi. Fusce dignissim justo ex, eget vulputate orci hendrerit a. Vivamus tortor nunc, laoreet sed faucibus vel, porttitor ac odio. Aenean eget turpis eros. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse vel consectetur risus. Aenean ultrices, eros eu facilisis laoreet, leo turpis feugiat risus, in luctus lorem lorem sed turpis. Phasellus non mauris ac neque tempus vulputate. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus."]]
     [:section
      [typography {:variant "h6"}
       "Account keeping fee"]
      [typography {:variant "body2"}
       "In faucibus, odio eget commodo fringilla, lectus purus convallis lorem, ac molestie risus quam sit amet est. Phasellus ac tellus libero. Curabitur dignissim fermentum metus, in ullamcorper massa aliquet eu. Praesent sit amet semper ipsum. Morbi ultricies, metus quis ultrices auctor, leo mauris dictum enim, at volutpat erat justo ultrices arcu. Fusce porttitor neque lobortis, semper libero sit amet, tristique ligula. Proin vel est fringilla, finibus lorem sit amet, malesuada neque. Duis tellus orci, maximus eu quam et, vehicula efficitur nulla. Vivamus facilisis nibh vel augue feugiat venenatis. Nunc in aliquet metus. Etiam at orci rutrum, aliquet sem vel, vulputate ex. Aenean posuere dui eget arcu molestie accumsan. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Suspendisse potenti."]]
     [:section
      [typography {:variant "h6"}
       "Expense recovery fee"]
      [typography {:variant "body2"}
       "Curabitur sit amet tempus dui. Praesent vitae elit nisl. Aenean eleifend eleifend faucibus. Suspendisse nec magna est. Phasellus massa neque, imperdiet id porta id, tristique ut diam. Pellentesque id turpis vehicula, semper neque sit amet, luctus dui. Donec volutpat leo eu risus tempor rhoncus. Integer placerat mi sed libero viverra aliquam. Proin consectetur dui eget pharetra volutpat. Cras in nunc eu ligula pharetra egestas. Morbi aliquam, felis non viverra condimentum, tellus lectus rutrum quam, et rutrum ante urna sit amet sapien. Ut lacus lorem, porttitor a velit sit amet, viverra pharetra eros."]]]))
