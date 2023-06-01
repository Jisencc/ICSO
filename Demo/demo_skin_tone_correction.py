import joblib
import pandas as pd
import numpy as np

# Change mean and var with model. e.g. If model is model_skin_tone(L)_corr, choosing mean_L and var_L. If model is model_skin_tone(a)_corr, choosing mean_a and var_a.
mean_L = [51.77083333, 117.50000001, 104.45833333, 57.89583333]
var_L = [130.70442708, 810.62499963, 663.91493028, 163.4127604]
mean_a = [11.54166667, 122.72916667, 108.85416666, 60.8125]
var_a = [14.99826389, 960.72526019, 732.77734369, 186.91623266]
mean_b = [12.58333333, 113.83333334, 100.41666666, 56.89583333]
var_b = [22.3125, 1038.62499965, 858.27083299, 188.0655382]
X = pd.read_csv(
    "skin_tone_face(L).csv")  # corresponding data should be chosen to choosed model. e.g. "skin_tone_face(L)/(a)/(b).csv" is corresponding with "model_skin_tone(L)/(a)/(b)_corr.h5"
label_final = X[
    "Label (L)"].copy()
X = X.drop("Label (L)",
           axis=1)
model = joblib.load(
    "model_skin_tone(L)_corr.h5")  # Change model with test data, e.g. model_skin_tone(L)/(a)/(b)_corr.h5 is corresponding with  "skin_tone_face(L)/(a)/(b).csv"
X_array = np.array(X)
X_std = (X_array - mean_L) / np.sqrt(var_L)
X_std[:, 0] = X["L"].array
result = model.predict(X_std)  # Now you can obtain result of correction for skin tone
