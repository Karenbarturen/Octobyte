--
-- PostgreSQL database dump
--

\restrict 1ubW5LABR1VNegFI8wk73WPLnj2YSm5bMY77J9c1o2Ifkbi233j2udwh6AvbqHH

-- Dumped from database version 18.6
-- Dumped by pg_dump version 18.6

-- Started on 2026-10-16 15:29:56

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 222 (class 1259 OID 16446)
-- Name:  Bateria; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public." Bateria" (
    "idBateria" integer NOT NULL,
    "energiaGuardada" integer NOT NULL,
    "nivelPorcentaje" integer NOT NULL,
    "idOperativo" integer NOT NULL
);


ALTER TABLE public." Bateria" OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16445)
-- Name:  Bateria_idBateria_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public." Bateria_idBateria_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public." Bateria_idBateria_seq" OWNER TO postgres;

--
-- TOC entry 5022 (class 0 OID 0)
-- Dependencies: 221
-- Name:  Bateria_idBateria_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public." Bateria_idBateria_seq" OWNED BY public." Bateria"."idBateria";


--
-- TOC entry 228 (class 1259 OID 16482)
-- Name: Electricidad; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Electricidad" (
    "idElectricidad" integer NOT NULL,
    voltaje integer NOT NULL,
    corriente integer NOT NULL,
    "generacionElectricidad" integer NOT NULL,
    "consumoElectricidad" integer NOT NULL
);


ALTER TABLE public."Electricidad" OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16481)
-- Name: Electricidad_idElectricidad_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Electricidad_idElectricidad_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Electricidad_idElectricidad_seq" OWNER TO postgres;

--
-- TOC entry 5023 (class 0 OID 0)
-- Dependencies: 227
-- Name: Electricidad_idElectricidad_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Electricidad_idElectricidad_seq" OWNED BY public."Electricidad"."idElectricidad";


--
-- TOC entry 225 (class 1259 OID 16467)
-- Name: Medicion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Medicion" (
    "idMedicion" integer NOT NULL,
    viento integer NOT NULL,
    temperatura integer NOT NULL,
    lumens integer NOT NULL
);


ALTER TABLE public."Medicion" OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16470)
-- Name: Medicion_idMedicion_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Medicion_idMedicion_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Medicion_idMedicion_seq" OWNER TO postgres;

--
-- TOC entry 5024 (class 0 OID 0)
-- Dependencies: 226
-- Name: Medicion_idMedicion_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Medicion_idMedicion_seq" OWNED BY public."Medicion"."idMedicion";


--
-- TOC entry 223 (class 1259 OID 16456)
-- Name: Operativo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Operativo" (
    "idOperativo" integer NOT NULL,
    nombre text
);


ALTER TABLE public."Operativo" OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16459)
-- Name: Operativo_idOperativo_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Operativo_idOperativo_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Operativo_idOperativo_seq" OWNER TO postgres;

--
-- TOC entry 5025 (class 0 OID 0)
-- Dependencies: 224
-- Name: Operativo_idOperativo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Operativo_idOperativo_seq" OWNED BY public."Operativo"."idOperativo";


--
-- TOC entry 219 (class 1259 OID 16424)
-- Name: Total_idTotal_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Total_idTotal_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."Total_idTotal_seq" OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16425)
-- Name: Total; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Total" (
    "idTotal" integer DEFAULT nextval('public."Total_idTotal_seq"'::regclass) NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "numAlerta" integer NOT NULL,
    "idMedicionTotal" integer NOT NULL
);


ALTER TABLE public."Total" OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16494)
-- Name: medicionTotal; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."medicionTotal" (
    "idMedicionTotal" integer NOT NULL,
    "idBateria" integer NOT NULL,
    "idElectricidad" integer NOT NULL,
    "idMedicion" integer NOT NULL
);


ALTER TABLE public."medicionTotal" OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16493)
-- Name: medicionTotal_idMedicionTotal_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."medicionTotal_idMedicionTotal_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."medicionTotal_idMedicionTotal_seq" OWNER TO postgres;

--
-- TOC entry 5026 (class 0 OID 0)
-- Dependencies: 229
-- Name: medicionTotal_idMedicionTotal_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."medicionTotal_idMedicionTotal_seq" OWNED BY public."medicionTotal"."idMedicionTotal";


--
-- TOC entry 4836 (class 2604 OID 16449)
-- Name:  Bateria idBateria; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public." Bateria" ALTER COLUMN "idBateria" SET DEFAULT nextval('public." Bateria_idBateria_seq"'::regclass);


--
-- TOC entry 4839 (class 2604 OID 16485)
-- Name: Electricidad idElectricidad; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Electricidad" ALTER COLUMN "idElectricidad" SET DEFAULT nextval('public."Electricidad_idElectricidad_seq"'::regclass);


--
-- TOC entry 4838 (class 2604 OID 16471)
-- Name: Medicion idMedicion; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Medicion" ALTER COLUMN "idMedicion" SET DEFAULT nextval('public."Medicion_idMedicion_seq"'::regclass);


--
-- TOC entry 4837 (class 2604 OID 16460)
-- Name: Operativo idOperativo; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Operativo" ALTER COLUMN "idOperativo" SET DEFAULT nextval('public."Operativo_idOperativo_seq"'::regclass);


--
-- TOC entry 4840 (class 2604 OID 16497)
-- Name: medicionTotal idMedicionTotal; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."medicionTotal" ALTER COLUMN "idMedicionTotal" SET DEFAULT nextval('public."medicionTotal_idMedicionTotal_seq"'::regclass);


--
-- TOC entry 5008 (class 0 OID 16446)
-- Dependencies: 222
-- Data for Name:  Bateria; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public." Bateria" ("idBateria", "energiaGuardada", "nivelPorcentaje", "idOperativo") FROM stdin;
\.


--
-- TOC entry 5014 (class 0 OID 16482)
-- Dependencies: 228
-- Data for Name: Electricidad; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Electricidad" ("idElectricidad", voltaje, corriente, "generacionElectricidad", "consumoElectricidad") FROM stdin;
\.


--
-- TOC entry 5011 (class 0 OID 16467)
-- Dependencies: 225
-- Data for Name: Medicion; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Medicion" ("idMedicion", viento, temperatura, lumens) FROM stdin;
\.


--
-- TOC entry 5009 (class 0 OID 16456)
-- Dependencies: 223
-- Data for Name: Operativo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Operativo" ("idOperativo", nombre) FROM stdin;
\.


--
-- TOC entry 5006 (class 0 OID 16425)
-- Dependencies: 220
-- Data for Name: Total; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Total" ("idTotal", fecha, "numAlerta", "idMedicionTotal") FROM stdin;
\.


--
-- TOC entry 5016 (class 0 OID 16494)
-- Dependencies: 230
-- Data for Name: medicionTotal; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."medicionTotal" ("idMedicionTotal", "idBateria", "idElectricidad", "idMedicion") FROM stdin;
\.


--
-- TOC entry 5027 (class 0 OID 0)
-- Dependencies: 221
-- Name:  Bateria_idBateria_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public." Bateria_idBateria_seq"', 1, false);


--
-- TOC entry 5028 (class 0 OID 0)
-- Dependencies: 227
-- Name: Electricidad_idElectricidad_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Electricidad_idElectricidad_seq"', 1, false);


--
-- TOC entry 5029 (class 0 OID 0)
-- Dependencies: 226
-- Name: Medicion_idMedicion_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Medicion_idMedicion_seq"', 1, false);


--
-- TOC entry 5030 (class 0 OID 0)
-- Dependencies: 224
-- Name: Operativo_idOperativo_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Operativo_idOperativo_seq"', 1, false);


--
-- TOC entry 5031 (class 0 OID 0)
-- Dependencies: 219
-- Name: Total_idTotal_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Total_idTotal_seq"', 1, false);


--
-- TOC entry 5032 (class 0 OID 0)
-- Dependencies: 229
-- Name: medicionTotal_idMedicionTotal_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."medicionTotal_idMedicionTotal_seq"', 1, false);


--
-- TOC entry 4844 (class 2606 OID 16452)
-- Name:  Bateria  Bateria_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public." Bateria"
    ADD CONSTRAINT " Bateria_pkey" PRIMARY KEY ("idBateria");


--
-- TOC entry 4850 (class 2606 OID 16492)
-- Name: Electricidad Electricidad_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Electricidad"
    ADD CONSTRAINT "Electricidad_pkey" PRIMARY KEY ("idElectricidad");


--
-- TOC entry 4848 (class 2606 OID 16480)
-- Name: Medicion Medicion_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Medicion"
    ADD CONSTRAINT "Medicion_pkey" PRIMARY KEY ("idMedicion");


--
-- TOC entry 4846 (class 2606 OID 16520)
-- Name: Operativo Operativo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Operativo"
    ADD CONSTRAINT "Operativo_pkey" PRIMARY KEY ("idOperativo");


--
-- TOC entry 4842 (class 2606 OID 16437)
-- Name: Total idTotal_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Total"
    ADD CONSTRAINT "idTotal_pkey" PRIMARY KEY ("idTotal");


--
-- TOC entry 4852 (class 2606 OID 16503)
-- Name: medicionTotal medicionTotal_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."medicionTotal"
    ADD CONSTRAINT "medicionTotal_pkey" PRIMARY KEY ("idMedicionTotal");


--
-- TOC entry 4855 (class 2606 OID 16504)
-- Name: medicionTotal idBateria; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."medicionTotal"
    ADD CONSTRAINT "idBateria" FOREIGN KEY ("idBateria") REFERENCES public." Bateria"("idBateria");


--
-- TOC entry 4856 (class 2606 OID 16509)
-- Name: medicionTotal idElectricidad; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."medicionTotal"
    ADD CONSTRAINT "idElectricidad" FOREIGN KEY ("idElectricidad") REFERENCES public."Electricidad"("idElectricidad");


--
-- TOC entry 4857 (class 2606 OID 16514)
-- Name: medicionTotal idMedicion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."medicionTotal"
    ADD CONSTRAINT "idMedicion" FOREIGN KEY ("idMedicion") REFERENCES public."Medicion"("idMedicion");


--
-- TOC entry 4853 (class 2606 OID 16527)
-- Name: Total idMedicionTotal; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Total"
    ADD CONSTRAINT "idMedicionTotal" FOREIGN KEY ("idMedicionTotal") REFERENCES public."medicionTotal"("idMedicionTotal") NOT VALID;


--
-- TOC entry 4854 (class 2606 OID 16521)
-- Name:  Bateria idOperativo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public." Bateria"
    ADD CONSTRAINT "idOperativo" FOREIGN KEY ("idOperativo") REFERENCES public."Operativo"("idOperativo") NOT VALID;


-- Completed on 2026-10-16 15:29:57

--
-- PostgreSQL database dump complete
--

\unrestrict 1ubW5LABR1VNegFI8wk73WPLnj2YSm5bMY77J9c1o2Ifkbi233j2udwh6AvbqHH

