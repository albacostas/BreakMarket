package com.albadiego.breakmarket.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
public class UnsupportedPatchPathException extends Exception{
    private List<String> paths;
}
