CREATE TABLE IF NOT EXISTS ie_pj_marcature (
    id_ie_pj_marcature BIGSERIAL PRIMARY KEY,
    data DATE,
    matricola TEXT,
    marcatura int,
    fl_consolidata int,
    tipo_marcatura TEXT);
