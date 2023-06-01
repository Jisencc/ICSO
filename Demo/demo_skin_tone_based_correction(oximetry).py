import pandas as pd
import os
import joblib
import numpy as np

model = joblib.load("model_oximeter_corr.h5")
oxygen_dataframe = pd.read_csv("data_oxymeter_corr.csv")
mean = [64.48461806, 6.0575, 14.27767361, 31.63413173, 98.79166667]
var = [75.83119629, 3.63777639, 5.10068593, 113.62073041, 1.05381945]
label_final = oxygen_dataframe["label"].copy()
oxygen_dataframe = oxygen_dataframe.drop("label", axis=1)
X_array = np.array(oxygen_dataframe)
X_std = (X_array - mean) / np.sqrt(var)
X_std[:, 4] = oxygen_dataframe["O"].array
result = model.predict(X_std)  # Now you can obtain result of correction for oximetry
