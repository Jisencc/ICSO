import pandas as pd
import os
import joblib
import numpy as np

model = joblib.load("model_app_corr.h5")
oxygen_dataframe = pd.read_csv("data_app_corr.csv")
mean = [64.9353125, 6.1353125, 14.8075, 30.6140073, 98.50870274, ]
var = [27.46616048, 1.4489591, 4.83798403, 38.62350951, 0.44073878]
label_final = oxygen_dataframe["label"].copy()
oxygen_dataframe = oxygen_dataframe.drop("label", axis=1)
X_array = np.array(oxygen_dataframe)
X_std = (X_array - mean) / np.sqrt(var)
X_std[:, 4] = oxygen_dataframe["pre"].array
result = model.predict(X_std)  # Now you can obtain result of correction for app prediction
