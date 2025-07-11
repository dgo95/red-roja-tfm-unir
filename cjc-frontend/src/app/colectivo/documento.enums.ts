export enum NivelDocumento {
    COLECTIVO,
    INTERMEDIO,
    CENTRAL,
}

export enum Confidencialidad { PUBLICO, INTERNO, RESTRINGIDO, CONFIDENCIAL }


export enum TipoDocumento {
    ACTA,
    ESTATUTO,
    COMUNICADO,
    FORMATIVO,
    TESIS,
    PLAN,
    RESOLUCION,
    INFORME,
    CIRCULAR,
    CARTA,
    PROPAGANDA,
    OTRO,
}

export enum Categoria {
    OBRERO,
    ESTUDIANTIL,
    POLITICO,
    ORGANIZACION,
    AGITACION,
    FORMACION,
    MUJER,
    VECINAL,
    OTRO,
}

export const NivelDocumentoLabel: Record<NivelDocumento, string> = {
    [NivelDocumento.COLECTIVO]: 'Colectivo de base',
    [NivelDocumento.INTERMEDIO]: 'Comité intermedio',
    [NivelDocumento.CENTRAL]: 'Comité central',
};

export const ConfidencialidadLabel: Record<Confidencialidad, string> = {
    [Confidencialidad.PUBLICO]: 'Público',
    [Confidencialidad.INTERNO]: 'Interno',
    [Confidencialidad.RESTRINGIDO]: 'Restringido',
    [Confidencialidad.CONFIDENCIAL]: 'Confidencial',
};

export const TipoDocumentoLabel: Record<TipoDocumento, string> = {
    [TipoDocumento.ACTA]: 'Acta',
    [TipoDocumento.ESTATUTO]: 'Estatuto',
    [TipoDocumento.COMUNICADO]: 'Comunicado',
    [TipoDocumento.FORMATIVO]: 'Formativo',
    [TipoDocumento.TESIS]: 'Tesis',
    [TipoDocumento.PLAN]: 'Plan de trabajo',
    [TipoDocumento.RESOLUCION]: 'Resolución',
    [TipoDocumento.INFORME]: 'Informe',
    [TipoDocumento.CIRCULAR]: 'Circular',
    [TipoDocumento.CARTA]: 'Carta',
    [TipoDocumento.PROPAGANDA]: 'Propaganda',
    [TipoDocumento.OTRO]: 'Otro',
};

export const CategoriaLabel: Record<Categoria, string> = {
    [Categoria.OBRERO]: 'Movimiento obrero',
    [Categoria.ESTUDIANTIL]: 'Movimiento estudiantil',
    [Categoria.POLITICO]: 'Político',
    [Categoria.ORGANIZACION]: 'Organización',
    [Categoria.AGITACION]: 'Agitación y propaganda',
    [Categoria.FORMACION]: 'Formación',
    [Categoria.MUJER]: 'Mujer trabajadora',
    [Categoria.VECINAL]: 'Movimiento vecinal',
    [Categoria.OTRO]: 'Otro',
};