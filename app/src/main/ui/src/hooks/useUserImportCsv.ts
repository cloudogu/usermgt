import {useEffect, useState} from "react";

export type CsvParseError = "NO_CSV_FILE" | undefined;

export default function useUserImportCsv(selectedFile: FileList | undefined) {
    const [header, setHeader] = useState([""]);
    const [rows, setRows] = useState([[""]]);
    const [fileSize, setFileSize] = useState(0);
    const [fileType, setFileType] = useState("");
    const [errors, setErrors] = useState<CsvParseError[] | undefined>(undefined);

    useEffect(() => {
        const file = selectedFile?.item(0) as File;
        if (file) {
            if (file.type !== "text/csv") {
                setErrors([...(errors ?? []), "NO_CSV_FILE"]);
            } else {
                file.text().then(text => {
                    const lines = text.split("\n");
                    if (lines.length > 1) {
                        setHeader(lines[0].split(","));
                        const csvContent: string[][] = [];
                        for (let i = 1; i < lines.length; i++) {
                            if (lines[i].length > 0) {
                                csvContent.push(lines[i].split(","));
                            }
                        }
                        setRows(csvContent);
                        setFileSize(file.size);
                        setFileType(file.type);
                    }
                });
            }
        } else {
            setHeader([]);
            setRows([]);
            setFileSize(0);
            setFileType("");
        }
    }, [selectedFile]);

    return {
        file: (fileSize === 0) ? undefined : {
            size: fileSize,
            type: fileType,
            header: header,
            rows: rows,
        },
        errors: errors,
    };
}