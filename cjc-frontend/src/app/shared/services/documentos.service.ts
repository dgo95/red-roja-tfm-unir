import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
    providedIn: 'root'
})
export class DocumentosService {
    exportToExcel(filteredData: any[]) {
        return this.http.post(`${this.documentosUrl}/api/archivos/xlsx/censoGeneral`, filteredData, { responseType: 'blob' });
    }
    constructor(private http: HttpClient) { }

    private readonly documentosUrl = environment.apiHost;

}