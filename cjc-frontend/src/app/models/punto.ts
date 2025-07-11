export class Punto {
    constructor(
        public id: number,
        public orden: number,
        public titulo: string,
        public descripcion: string = "",
        public subpuntos: Subpunto[] = [],
    ) {}
}

export class Subpunto {
    constructor(
        public id: number,
        public orden: number,
        public titulo: string,
        public descripcion: string = ""
    ) {}
}