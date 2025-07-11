import { Invitado } from "./invitado";
import { Punto } from "./punto";

export class Reunion {
    id: number = 0;
    fechaInicio: Date = new Date();
    duracion: number = 2;
    direccion: string = "";
    premilitantes: boolean = true;
    puntos: Punto[] = [];
    invitados: Invitado[] = [];


    constructor() { }
}