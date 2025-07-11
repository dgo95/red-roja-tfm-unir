// src/app/shared/role-map.ts
export type ClaveResponsabilidad =
    | 'POLITICO'   //  ➜ DIR_POLITICA
    | 'ORGANIZACION'
    | 'FINANZAS'
    | 'FORMACION'
    | 'AGIT'
    | 'MES'
    | 'MOS'
    | 'MUJ'
    | 'VECINAL'
    | 'FRENTE';

/** Front ➜ Back */
export const ROLE_MAP: Record<ClaveResponsabilidad, string> = {
    POLITICO: 'DIR_POLITICA',
    ORGANIZACION: 'ORGANIZACION',
    FINANZAS: 'FINANZAS',
    FORMACION: 'FORMACION',
    AGIT: 'AGITPROP',
    MES: 'ESTUDIANTIL',
    MOS: 'OBRERO',
    MUJ: 'MUJER',
    VECINAL: 'VECINAL',
    FRENTE: 'MASAS',
};

/** Back ➜ Front (se construye a partir del anterior) */
export const ROLE_MAP_INV: Record<string, ClaveResponsabilidad> =
    Object.fromEntries(Object.entries(ROLE_MAP).map(([k, v]) => [v, k])) as any;
