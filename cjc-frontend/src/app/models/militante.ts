import { Comite } from "./colectivo";
import { Select } from "./select";
//clase que representa un militante
export class Militante {
    nombre:string = "";
    apellido:string = "";
    apellido2:string = "";
    fechaNacimiento:string = "";
    numeroCarnet:string = "";
    telefono:string = "";
    email:string = "";
    roles:string [] = [];
    sexo:string = "";
    comiteBase:Select = new Select();
    responsabilidades:string[] = [];
    militanteId: string = "";
    comites: Select[] = [];
    habilidades: Select[] = [];
    mapaHabilidades: Map<number, string> = new Map<number, string>();

}